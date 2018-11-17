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
        DownloadService.progressRelay
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeLatestCache({ view, task ->
                    view.onTaskProcess(task)
                })
    }

    fun load(mangaId: Long) {
        manga = mangaDao.load(mangaId)
        taskDao.listInRx(mangaId)
                .toObservable()
                .doOnNext {
                    updateTaskList(it)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribeLatestCache({ view, list ->
                    view.onTaskLoadSuccess(list)
                }, { view, error ->
                    view.onTaskLoadError(error)
                })
    }

    private fun updateTaskList(list: List<Task>) {
        val manga = manga ?: return
        for (task in list) {
            task.mangaUrl = manga.url
            task.sourceId = manga.sourceId
            task.state = if (task.isFinish) Task.STATE_FINISH else Task.STATE_PAUSE
        }
    }
}