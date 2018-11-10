package top.rechinx.meow.ui.details

import android.os.Bundle
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.SChapter
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.repository.ChapterPager
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.mvp.RxPresenter
import top.rechinx.meow.ui.details.items.ChapterItem
import top.rechinx.meow.ui.reader.ReaderActivity
import top.rechinx.rikka.mvp.BasePresenter
import top.rechinx.rikka.mvp.MvpAppCompatActivity
import top.rechinx.rikka.rxbus.RxBus

class DetailPresenter(val sourceId: Long, val cid: String): BasePresenter<DetailActivity>(), KoinComponent {

    val sourceManager by inject<SourceManager>()

    val chapterDao by inject<ChapterDao> ()

    val source = sourceManager.get(sourceId) as Source

    val rxbusRelay: PublishRelay<Manga> by lazy { PublishRelay.create<Manga>() }

    var manga: Manga? = null

    private val mangaRepository: MangaRepository by inject()

    private lateinit var pager: ChapterPager

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        rxbusRelay.observeOn(AndroidSchedulers.mainThread())
                .subscribeReplay({ view, it ->
                    view.setLastChanged(it)
                })
        add(RxBus.instance?.register(Manga::class.java, Consumer {
            rxbusRelay.accept(it)
        }, Consumer {}, Action {}, Consumer {}))
    }

    fun restartPager() {
        pager = ChapterPager(source, cid)
        pager.results.observeOn(Schedulers.io())
                .map {
                    it.first to it.second.map { chapter -> networkToLocalChapter(chapter, manga?.id!!) }
                }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    it.first to it.second.map { chapter ->  ChapterItem(chapter) }
                }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeReplay({ view , (page, chapters) ->
                    view.onAddPage(page, chapters)
                }, { _, error ->
                    error.printStackTrace()
                })
        requestNext()
    }

    fun requestNext() {
        if(!hasNextPage()) return
        Observable.defer { pager.requestNext() }
                .subscribeFirst({ _, _ -> }, DetailActivity::onAddPageError)
    }

    fun hasNextPage(): Boolean {
        return pager.hasNextPage
    }

    fun markedAsHistory(manga: Manga) {
        manga.history = true
        add(mangaRepository.updateManga(manga))
    }

    fun favoriteOrNot() {
        val manga = manga ?: return
        manga.favorite = !manga.favorite
        add(mangaRepository.updateManga(manga))
    }

    fun fetchMangaInfo(sourceId: Long, cid: String) {
        mangaRepository.fetchMangaInfo(sourceId, cid)
                .subscribeFirst({ view, smange ->
                    this.manga = smange
                    view.onMangaLoadCompleted(smange)
                    view.setLastChanged(smange)
                }, { view, error ->
                    error.printStackTrace()
                    view.onMangaFetchError()
                })
    }

    fun networkToLocalChapter(sChapter: SChapter, mangaId: Long): Chapter {
        var localChapter = chapterDao.getChapter(sChapter.url!!, mangaId)
        if(localChapter == null) {
            val newChapter = Chapter.create().apply {
                manga_id = mangaId
            }
            newChapter.copyFrom(sChapter)
            val insertedId = chapterDao.insertChapter(newChapter)
            newChapter.id = insertedId
            localChapter = newChapter
        }
        return localChapter
    }

}