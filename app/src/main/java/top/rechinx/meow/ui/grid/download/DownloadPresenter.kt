package top.rechinx.meow.ui.grid.download

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.rikka.mvp.BasePresenter

class DownloadPresenter: BasePresenter<DownloadFragment>(), KoinComponent {

    val dao by inject<MangaDao>()

    fun load() {
        dao.listDownload()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribeLatestCache({ view, list ->
                    view.onDownloadLoaded(list)
                }, { view, error ->
                    view.onLoadError(error)
                })
    }
}