package top.rechinx.meow.ui.task

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.database.dao.TaskDao
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.download.DownloadService
import top.rechinx.rikka.mvp.BasePresenter

class TaskPresenter : BasePresenter<TaskActivity>(), KoinComponent {

    val taskDao by inject<TaskDao>()

    val mangaDao by inject<MangaDao>()

    var manga: Manga? = null

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        DownloadService.stateRelay
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeReplay({ view, (event, task) ->
                    when(event) {
                        DownloadService.EVENT_PROCESS -> view.onTaskProcess(task)
                        DownloadService.EVENT_PAUSE -> view.onTaskPause(task)
                        DownloadService.EVENT_PARSE -> view.onTaskParse(task)
                        DownloadService.EVENT_ERROR -> view.onTaskError(task)
                    }
                })
    }

    fun load(mangaId: Long) {
        manga = mangaDao.load(mangaId)
        taskDao.listInRx(mangaId)
                .toObservable()
                .doOnNext {
                    updateTaskList(it)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribeFirst({ view, list ->
                    view.onTaskLoadSuccess(list)
                }, { view, error ->
                    view.onTaskLoadError(error)
                })
    }

    private fun updateTaskList(list: List<Task>) {
        val manga = manga ?: return
        for (task in list) {
            task.sourceId = manga.sourceId
            if(task.isFinish) task.state = Task.STATE_FINISH
        }
    }
}