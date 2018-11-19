package top.rechinx.meow.ui.task

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.download.DownloadService
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.log.L
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.reader.ReaderActivity
import top.rechinx.rikka.mvp.MvpAppCompatActivity
import top.rechinx.rikka.mvp.factory.RequiresPresenter
import kotlin.collections.ArrayList

@RequiresPresenter(TaskPresenter::class)
class TaskActivity: MvpAppCompatActivity<TaskPresenter>(), BaseAdapter.OnItemClickListener {

    val mangaId by lazy { intent.getLongExtra(Extras.EXTRA_MANGA_ID, -1) }

    val chapterDao by inject<ChapterDao>()

    lateinit var adapter: TaskAdapter

    private var connection: ServiceConnection? = null

    private lateinit var binder: DownloadService.DownloadServiceBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        // Toolbar
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = getString(R.string.title_activity_task)
        customToolbar.setNavigationOnClickListener { finish() }
        // For recycler
        adapter = TaskAdapter(this, ArrayList())
        adapter.setOnItemClickListener(this)
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.itemAnimator = null
        recycler.addItemDecoration(adapter.getItemDecoration()!!)
        recycler.adapter = adapter
        // Init datas
        presenter.load(mangaId)
    }

    override fun onItemClick(view: View, position: Int) {
        val task = adapter.getItem(position)
        L.d(task.state.toString())
        when(task.state) {
            Task.STATE_FINISH -> {
                val chapter = chapterDao.getChapter(task.chapterId)
                chapter?.let { startReader(it) }
            }
            Task.STATE_PAUSE, Task.STATE_ERROR -> {
                task.chapter = chapterDao.getChapter(task.chapterId)
                task.state = Task.STATE_WAIT
                adapter.notifyItemChanged(position)
                val taskIntent = DownloadService.createIntent(this, task)
                startService(taskIntent)
            }
            Task.STATE_WAIT -> {
                task.state = Task.STATE_PAUSE
                adapter.notifyItemChanged(position)
                binder.service.removeDownload(task.id)
            }
            Task.STATE_DOING, Task.STATE_PARSE -> {
                binder.service.removeDownload(task.id)
            }
        }
    }

    fun onTaskLoadSuccess(list: List<Task>) {
        adapter.addAll(list)
        connection = object : ServiceConnection {
            override fun onServiceDisconnected(name: ComponentName?) {}

            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                binder = service as DownloadService.DownloadServiceBinder
                binder.service.initTask(list)
            }

        }
        bindService(Intent(this, DownloadService::class.java), connection, BIND_AUTO_CREATE)
    }

    fun onTaskLoadError(error: Throwable) {

    }

    fun onTaskProcess(task: Task) {
        val pos = adapter.getPositionById(task.id)
        if(pos != -1) {
            val item = adapter.getItem(pos)
            item.max = task.max
            item.progress = task.progress
            if (item.state != Task.STATE_PAUSE) {
                item.state = if (task.max == task.progress) Task.STATE_FINISH else Task.STATE_DOING
            }
            notifyItemChanged(pos)
        }
    }

    fun onTaskPause(task: Task) {
        val pos = adapter.getPositionById(task.id)
        if(pos != -1) {
            adapter.getItem(pos).state = Task.STATE_PAUSE
            notifyItemChanged(pos)
        }
    }

    fun onTaskParse(task: Task) {
        val position = adapter.getPositionById(task.id)
        if (position != -1) {
            val item = adapter.getItem(position)
            if (item.state != Task.STATE_PAUSE) {
                item.state = Task.STATE_PARSE
                notifyItemChanged(position)
            }
        }
    }

    fun onTaskError(task: Task) {
        val position = adapter.getPositionById(task.id)
        if (position != -1) {
            val item = adapter.getItem(position)
            if (item.state != Task.STATE_PAUSE) {
                item.state = Task.STATE_ERROR
                notifyItemChanged(position)
            }
        }
    }

    fun setLastChanged(manga: Manga) {
        presenter.manga?.last_read_chapter_id = manga.last_read_chapter_id
        adapter.setLast(manga.last_read_chapter_id)
    }

    private fun startReader(chapter: Chapter, isContinued: Boolean = false) {
        val manga = presenter.manga ?: return
        val intent = ReaderActivity.createIntent(this, manga, chapter, isContinued)
        startActivity(intent)
    }

    private fun notifyItemChanged(position: Int) {
        if(!recycler.isComputingLayout) {
            adapter.notifyItemChanged(position)
        }
    }

    companion object {

        fun createIntent(context: Context, mangaId: Long) : Intent {
            val intent = Intent(context, TaskActivity::class.java)
            intent.putExtra(Extras.EXTRA_MANGA_ID, mangaId)
            return intent
        }
    }
}