package top.rechinx.meow.ui.grid.download

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.database.dao.TaskDao
import top.rechinx.meow.data.download.DownloadProvider
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.rikka.mvp.BasePresenter

class DownloadPresenter: BasePresenter<DownloadFragment>(), KoinComponent {

    val mangaDao by inject<MangaDao>()

    val taskDao by inject<TaskDao>()

    val chapterDao by inject<ChapterDao>()

    val sourceManager by inject<SourceManager>()

    val preferences by inject<PreferenceHelper>()

    fun load() {
        mangaDao.listDownload()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .toObservable()
                .subscribeLatestCache({ view, list ->
                    view.onDownloadLoaded(list)
                }, { view, error ->
                    view.onLoadError(error)
                })
    }

    fun deleteDownloadedManga(mangaIds: List<Long>) {
        Observable.just(mangaIds)
                .doOnNext {
                    for (id in it) {
                        val manga = mangaDao.load(id)
                        chapterDao.updateChapterDownloadInfoByMangaId(id)
                        taskDao.deleteByMangaId(id)
                        if(manga != null) {
                            manga.download = false
                            mangaDao.updateManga(manga)
                            DownloadProvider.deleteMangaDirectory(preferences.downloadsDirectory().getOrDefault(),
                                    manga,
                                    sourceManager.getOrStub(manga.sourceId))
                        }
                    }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeFirst({ view, ids ->
                    view.onDownloadDeleted(ids)
                })
    }
}