package top.rechinx.meow.ui.grid.favorite

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.mvp.RxPresenter
import top.rechinx.rikka.mvp.BasePresenter

class FavoritePresenter: BasePresenter<FavoriteFragment>(), KoinComponent {

    private val mangaRepository: MangaRepository by inject()

    fun load() {
        mangaRepository.listFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribeLatestCache({ view, manga ->
                    view.onMangasLoaded(manga)
                }, FavoriteFragment::onMangasLoadError)
    }

}