package top.rechinx.meow.data.download

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.util.Pair
import android.webkit.MimeTypeMap
import androidx.collection.LongSparseArray
import com.hippo.unifile.UniFile
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Response
import org.koin.android.ext.android.inject
import top.rechinx.meow.App
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.MangaPage
import top.rechinx.meow.data.database.dao.TaskDao
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.meow.global.Extras
import top.rechinx.meow.utils.ImageUtil
import top.rechinx.meow.utils.RetryWithDelay
import top.rechinx.rikka.ext.saveTo
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class DownloadService: Service() {

    private lateinit var executorService: ExecutorService

    private val sourceManager by inject<SourceManager>()

    private val preferences by inject<PreferenceHelper>()

    private val taskDao by inject<TaskDao>()

    private var workerArray: LongSparseArray<Pair<Worker, Future<*>>> = LongSparseArray()

    override fun onCreate() {
        super.onCreate()
        executorService = Executors.newFixedThreadPool(4)
    }

    override fun onBind(intent: Intent?): IBinder {
        return DownloadServiceBinder()
    }


    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if(intent != null) {
            runningRelay.accept(true)
            val list = intent.getParcelableArrayListExtra<Task>(Extras.EXTRA_TASK)
            for (task in list) {
                val worker = Worker(task)
                val future = executorService.submit(worker)
                addWorker(task.id!!, worker, future)
            }
        }
        return super.onStartCommand(intent, flags, startId)
    }

    @Synchronized
    private fun addWorker(id: Long, worker: Worker, future: Future<*>) {
        if(workerArray[id] == null) {
            workerArray.put(id, Pair.create(worker, future))
        }
    }

    @Synchronized
    fun initTask(list: List<Task>) {
        for (task in list) {
            val pair = workerArray.get(task.id!!)
            if (pair != null) {
                task.state = pair.first.task.state
            }
        }
    }

    @Synchronized
    private fun completeDownload(id: Long) {
        workerArray.remove(id)
        if (workerArray.size() == 0) {
            notifyCompleted()
            stopSelf()
        }
    }

    private fun notifyCompleted() {
        workerArray.clear()
        runningRelay.accept(false)
    }

    inner class Worker(val task: Task) : Runnable {

        private val source = sourceManager.getOrStub(task.sourceId)

        override fun run() {
            try {
                val list = source.fetchMangaPages(task.chapter!!).blockingFirst()
                if(list.isNotEmpty()) {
                    val dir = DownloaderProvider.updateChapterIndex(contentResolver, rootDirectory(), task)
                    if(dir != null) {
                        task.max = list.size
                        task.state = Task.STATE_DOING
                        var success = true
                        for (i in task.progress until list.size) {
                            onDownloadProgress(i)
                            getOrDownloadImage(list[i], source as HttpSource, dir)
                                    .blockingSubscribe({}, { success = false})
                        }
                        if(success) {
                            onDownloadProgress(list.size)
                        }
                    } else {

                    }
                } else {

                }
            } catch (e : Exception) {

            }
            completeDownload(task.id!!)
        }

        private fun onDownloadProgress(progress: Int) {
            task.progress = progress
            taskDao.update(task)
            progressRelay.accept(task)
        }

        fun rootDirectory() : UniFile {
            return UniFile.fromUri(App.instance, Uri.parse(preferences.downloadsDirectory().getOrDefault()))
        }

        /**
         * Returns the observable which gets the image from the filesystem if it exists or downloads it
         * otherwise.
         *
         * @param page the page to download.
         * @param download the download of the page.
         * @param tmpDir the temporary directory of the download.
         */
        fun getOrDownloadImage(page: MangaPage, source: HttpSource, dir: UniFile): Observable<MangaPage> {
            if(page.imageUrl == null) return Observable.just(page)
            val filename = String.format("%03d", page.number)
            val tmpFile = dir.findFile("$filename.tmp")

            // Delete temp file if it exists.
            tmpFile?.delete()

            // Try to find the image file.
            val imageFile = dir.listFiles()!!.find { it.name!!.startsWith("$filename.") }

            // If the image is already downloaded, do nothing. Otherwise download from network
            val pageObservable = if (imageFile != null)
                Observable.just(imageFile)
            else {
                page.status = MangaPage.DOWNLOAD_IMAGE
                page.progress = 0
                source.fetchImage(page)
                        .map { response ->
                            val file = dir.createFile("$filename.tmp")
                            try {
                                response.body()!!.source().saveTo(file.openOutputStream())
                                val extension = getImageExtension(response, file)
                                file.renameTo("$filename.$extension")
                            } catch (e: Exception) {
                                response.close()
                                file.delete()
                                throw e
                            }
                            file
                        }
                        // Retry 3 times, waiting 2, 4 and 8 seconds between attempts.
                        .retryWhen(RetryWithDelay(3, { (2 shl it - 1) * 1000 }, Schedulers.trampoline()))
            }

            return pageObservable
                    // When the image is ready, set image path, progress (just in case) and status
                    .doOnNext { file ->
                        page.progress = 100
                        page.status = MangaPage.READY
                    }
                    .map { page }
                    // Mark this page as error and allow to download the remaining
                    .onErrorReturn {
                        page.progress = 0
                        page.status = MangaPage.ERROR
                        page
                    }
        }

        /**
         * Returns the extension of the downloaded image from the network response, or if it's null,
         * analyze the file. If everything fails, assume it's a jpg.
         *
         * @param response the network response of the image.
         * @param file the file where the image is already downloaded.
         */
        private fun getImageExtension(response: Response, file: UniFile): String {
            // Read content type if available.
            val mime = response.body()?.contentType()?.let { ct -> "${ct.type()}/${ct.subtype()}" }
            // Else guess from the uri.
                    ?:contentResolver.getType(file.uri)
                    // Else read magic numbers.
                    ?: ImageUtil.findImageType { file.openInputStream() }?.mime

            return MimeTypeMap.getSingleton().getExtensionFromMimeType(mime) ?: "jpg"
        }
    }
    inner class DownloadServiceBinder : Binder() {
        val service: DownloadService
            get() = this@DownloadService

    }

    companion object {

        val runningRelay = BehaviorRelay.createDefault(false)
        val progressRelay = PublishRelay.create<Task>()

        fun createIntent(context: Context, list: ArrayList<Task>): Intent {
            val intent = Intent(context, DownloadService::class.java)
            intent.putParcelableArrayListExtra(Extras.EXTRA_TASK, list)
            return intent
        }
    }
}