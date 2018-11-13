package top.rechinx.meow.ui.grid.history

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.rikka.mvp.BasePresenter

class HistoryPresenter: BasePresenter<HistoryFragment>(), KoinComponent {

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

}