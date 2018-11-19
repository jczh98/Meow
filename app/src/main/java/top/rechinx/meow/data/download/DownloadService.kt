package top.rechinx.meow.data.download

import android.app.Service
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.IBinder
import android.webkit.MimeTypeMap
import androidx.collection.LongSparseArray
import com.hippo.unifile.UniFile
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import okhttp3.Request
import okhttp3.Response
import org.koin.android.ext.android.inject
import top.rechinx.meow.App
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.MangaPage
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.dao.TaskDao
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.log.L
import top.rechinx.meow.utils.ImageUtil
import top.rechinx.meow.utils.RetryWithDelay
import top.rechinx.rikka.ext.saveTo
import java.io.IOException
import java.io.InterruptedIOException
import java.net.SocketTimeoutException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class DownloadService: Service() {

    private lateinit var executorService: ExecutorService

    private val sourceManager by inject<SourceManager>()

    private val preferences by inject<PreferenceHelper>()

    private val taskDao by inject<TaskDao>()

    private val chapterDao by inject<ChapterDao>()

    private var workerArray: LongSparseArray<Pair<Worker, Future<*>>> = LongSparseArray()

    override fun onCreate() {
        super.onCreate()
        executorService = Executors.newFixedThreadPool(1)
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
            workerArray.put(id, Pair(worker, future))
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
    private fun completeDownload(task: Task) {
        if(task.chapter != null) {
            task.chapter!!.complete = true
            chapterDao.updateChapter(task.chapter!!)
        }
        workerArray.remove(task.id)
        if (workerArray.size() == 0) {
            notifyCompleted()
            stopSelf()
        }
    }

    private fun notifyCompleted() {
        workerArray.clear()
        runningRelay.accept(false)
    }

    @Synchronized
    fun removeDownload(id: Long) {
        val pair = workerArray.get(id)
        if(pair != null) {
            pair.second.cancel(true)
            workerArray.remove(id)
        }
    }

    inner class Worker(val task: Task) : Runnable {

        private val source = sourceManager.getOrStub(task.sourceId)

        override fun run() {
            try {
                // listen to parse relay
                task.state = Task.STATE_PARSE
                stateRelay.accept(Pair(EVENT_PARSE, task))
                val list = source.fetchMangaPages(task.chapter!!)
                        .subscribeOn(Schedulers.trampoline())
                        .doOnError { stateRelay.accept(Pair(EVENT_ERROR, task)) }
                        .blockingFirst()
                if(list.isNotEmpty()) {
                    val dir = DownloaderProvider.updateChapterIndex(contentResolver, rootDirectory(), task)
                    if(dir != null) {
                        task.max = list.size
                        task.state = Task.STATE_DOING
                        var success = false
                        for (i in task.progress until list.size) {
                            onDownloadProgress(i)
                            success = false
                            for (retry in 0 until 3) {
                                success = getOrDownloadImage(list[i], source as HttpSource, dir)
                                if(!success) {
                                    Thread.sleep( (2 shl retry) * 1000L )
                                } else {
                                    break
                                }
                            }
                            if(!success) {
                                stateRelay.accept(Pair(EVENT_ERROR, task))
                            }
                        }
                        if(success) {
                            onDownloadProgress(list.size)
                        }
                    } else {
                        stateRelay.accept(Pair(EVENT_ERROR, task))
                    }
                } else {
                    stateRelay.accept(Pair(EVENT_ERROR, task))
                }
            } catch (e : InterruptedIOException) {
                onDownloadPaused(task)
            }
            completeDownload(task)
        }

        private fun onDownloadPaused(task: Task) {
            task.state = Task.STATE_PAUSE
            taskDao.update(task)
            stateRelay.accept(Pair(EVENT_PAUSE, task))
        }

        private fun onDownloadProgress(progress: Int) {
            task.progress = progress
            taskDao.update(task)
            stateRelay.accept(Pair(EVENT_PROCESS, task))
        }

        fun rootDirectory() : UniFile {
            return UniFile.fromUri(App.instance, Uri.parse(preferences.downloadsDirectory().getOrDefault()))
        }

        /**
         * Returns the existences of downloaded image from network or local filesystem
         *
         * @param page the page to download.
         * @param download the download of the page.
         * @param tmpDir the temporary directory of the download.
         */
        @Throws(InterruptedIOException::class)
        fun getOrDownloadImage(page: MangaPage, source: HttpSource, dir: UniFile): Boolean {
            if(page.imageUrl == null) return false
            val filename = String.format("%03d", page.number)
            val tmpFile = dir.findFile("$filename.tmp")

            // Delete temp file if it exists.
            tmpFile?.delete()

            // Try to find the image file.
            val imageFile = dir.listFiles()!!.find { it.name!!.startsWith("$filename.") }

            if(imageFile != null) {
                return true
            } else {
                var response : Response? = null
                val file = dir.createFile("$filename.tmp")
                try {
                    response = source.fetchStraightImage(page)
                    if(response.isSuccessful) {
                        response.body()!!.source().saveTo(file.openOutputStream())
                        val extension = getImageExtension(response, file)
                        file.renameTo("$filename.$extension")
                        return true
                    }
                } catch (e : SocketTimeoutException) {
                    file.delete()
                    e.printStackTrace()
                } catch (e : InterruptedIOException) {
                    file.delete()
                    throw e
                } catch (e: IOException) {
                    file.delete()
                    e.printStackTrace()
                } finally {
                    response?.close()
                }
            }
            return false
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

        const val EVENT_PROCESS = 1
        const val EVENT_PAUSE = 2
        const val EVENT_PARSE = 3
        const val EVENT_ERROR = 4

        val runningRelay = BehaviorRelay.createDefault(false)

        val stateRelay = PublishRelay.create<Pair<Int, Task>>()
        
        fun createIntent(context: Context, task: Task): Intent {
            return createIntent(context, arrayListOf(task))
        }

        fun createIntent(context: Context, list: ArrayList<Task>): Intent {
            val intent = Intent(context, DownloadService::class.java)
            intent.putParcelableArrayListExtra(Extras.EXTRA_TASK, list)
            return intent
        }
    }
}