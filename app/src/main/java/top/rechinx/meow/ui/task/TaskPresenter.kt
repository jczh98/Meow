package top.rechinx.meow.ui.task

import android.os.Bundle
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.database.dao.TaskDao
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.download.DownloadService
import top.rechinx.rikka.mvp.BasePresenter
import top.rechinx.rikka.rxbus.RxBus

class TaskPresenter : BasePresenter<TaskActivity>(), KoinComponent {

    val taskDao by inject<TaskDao>()

    val mangaDao by inject<MangaDao>()

    var manga: Manga? = null

    val rxbusRelay: PublishRelay<Manga> by lazy { PublishRelay.create<Manga>() }

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
        // Last updated chapters shows
        rxbusRelay.observeOn(AndroidSchedulers.mainThread())
                .subscribeReplay({ view, it ->
                    view.setLastChanged(it)
                })
        add(RxBus.instance?.register(Manga::class.java, Consumer {
            rxbusRelay.accept(it)
        }, Consumer {}, Action {}, Consumer {}))
    }

    fun load(mangaId: Long) {
        manga = mangaDao.load(mangaId)
        taskDao.listInRx(mangaId)
                .toObservable()
                .doOnNext {
                    updateTaskList(it)
                }.observeOn(AndroidSchedulers.mainThread())
                .subscribeFirst({ view, list ->
                    if(manga != null) {
                        view.setLastChanged(manga!!)
                    }
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