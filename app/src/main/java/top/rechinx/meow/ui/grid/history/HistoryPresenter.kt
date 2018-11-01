package top.rechinx.meow.ui.grid.history

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.mvp.RxPresenter

class HistoryPresenter(private val mangaRepository: MangaRepository): HistoryContract.Presenter, RxPresenter<HistoryContract.View>() {

    override fun load() {
        rx {
            mangaRepository.listHistory()
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