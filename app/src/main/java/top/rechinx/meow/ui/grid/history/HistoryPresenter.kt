package top.rechinx.meow.ui.grid.history

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.download.DownloadProvider
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.rikka.mvp.BasePresenter

class HistoryPresenter: BasePresenter<HistoryFragment>(), KoinComponent {

    val mangaDao by inject<MangaDao>()

    private val mangaRepository: MangaRepository by inject()

    fun load() {
        mangaRepository.listHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribeLatestCache({ view, manga ->
                    view.onMangasLoaded(manga)
                }, HistoryFragment::onMangasLoadError)
    }

    fun deleteHistoryManga(mangaIds: List<Long>) {
        Observable.just(mangaIds)
                .doOnNext {
                    for (id in it) {
                        val manga = mangaDao.load(id)
                        if(manga != null) {
                            manga.history = false
                            mangaDao.updateManga(manga)
                        }
                    }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeFirst({ view, ids ->
                    view.onHistoryDeleted(ids)
                })
    }

}