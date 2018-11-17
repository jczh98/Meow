package top.rechinx.meow.ui.task

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_task.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.download.DownloadService
import top.rechinx.meow.global.Extras
import top.rechinx.rikka.mvp.MvpAppCompatActivity
import top.rechinx.rikka.mvp.factory.RequiresPresenter
import kotlin.collections.ArrayList

@RequiresPresenter(TaskPresenter::class)
class TaskActivity: MvpAppCompatActivity<TaskPresenter>() {

    val mangaId by lazy { intent.getLongExtra(Extras.EXTRA_MANGA_ID, -1) }

    lateinit var adapter: TaskAdapter

    private var connection: ServiceConnection? = null

    private lateinit var binder: DownloadService.DownloadServiceBinder

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        // Toolbar
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        customToolbar.setNavigationOnClickListener { finish() }
        // For recycler
        adapter = TaskAdapter(this, ArrayList())
        recycler.layoutManager = LinearLayoutManager(this)
        recycler.setHasFixedSize(true)
        recycler.itemAnimator = null
        recycler.addItemDecoration(adapter.getItemDecoration()!!)
        recycler.adapter = adapter
        // Init datas
        presenter.load(mangaId)
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
        val pos = adapter.getPositionById(task.id!!)
        if(pos != -1) {
            val item = adapter.getItem(pos)
            item.max = task.max
            item.progress = task.progress
            notifyItemChanged(pos)
        }
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