package top.rechinx.meow.ui.grid.favorite

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.mvp.RxPresenter

class FavoritePresenter(private val mangaRepository: MangaRepository): FavoriteContract.Presenter, RxPresenter<FavoriteContract.View>() {

    override fun load() {
        rx {
            mangaRepository.listFavorite()
                    .subscribeOn(Schedulers.io())
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe({
                        view?.onMangasLoaded(it)
                    }, {
                        view?.onMangasLoadError()
                    })
        }
    }

}