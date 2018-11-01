package top.rechinx.meow.ui.reader

import com.jakewharton.rxrelay2.BehaviorRelay
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.repository.ChapterRepository
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.mvp.RxPresenter
import top.rechinx.meow.ui.reader.loader.ChapterLoader
import top.rechinx.meow.ui.reader.model.ReaderChapter
import top.rechinx.meow.ui.reader.model.ReaderPage
import top.rechinx.meow.ui.reader.model.ViewerChapters
import java.util.*
import java.util.concurrent.TimeUnit

class ReaderPresenter(private val sourceManager: SourceManager,
                      private val mangaRepository: MangaRepository,
                      private val chapterRepository: ChapterRepository) : RxPresenter<ReaderContarct.View>(), ReaderContarct.Presenter {

    var manga: Manga? = null
        private set

    private val chapterList by lazy {
        val manga = manga!!
        val chapters = chapterRepository.getLocalChapters(manga).reversed()
        chapters.map(::ReaderChapter)
    }

    private val viewerChaptersRelay = BehaviorRelay.create<ViewerChapters>()
    private var chapterId = -1L

    private var activeChapterDisposable: Disposable ?= null

    private var loader: ChapterLoader? = null

    override fun needsInit() : Boolean = manga == null

    override fun loadInit(mangaId: Long, initialChapterId: Long) {
        if(!needsInit()) return

        rx {
            mangaRepository.getManga(mangaId)
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { loadInit(it, initialChapterId) }
                    .subscribe()
        }
    }

    fun loadInit(manga: Manga, initialChapterId: Long) {
        this.manga = manga
        if(chapterId == -1L) chapterId = initialChapterId

        val source = sourceManager.get(manga.sourceId)
        loader = ChapterLoader(manga, source!!)

        rx { Observable.just(manga).subscribe { view?.setManga(it)} }
        rx { viewerChaptersRelay.subscribe{ view?.setChapters(it) }}

        activeChapterDisposable?.dispose()
        activeChapterDisposable = Observable
                .fromCallable { chapterList.first{ chapterId == it.chapter.id} }
                .flatMap { getLoadObservable(loader!!, it) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    private fun getLoadObservable(loader: ChapterLoader, chapter: ReaderChapter): Observable<ViewerChapters> {
        return loader.loadChapter(chapter)
                .andThen(Observable.fromCallable {
                    val chapterPos = chapterList.indexOf(chapter)
                    ViewerChapters(chapter,
                            chapterList.getOrNull(chapterPos - 1),
                            chapterList.getOrNull(chapterPos + 1))
                })
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext { newChapters ->
                    val oldChapters = viewerChaptersRelay.value
                    newChapters.ref()
                    oldChapters?.unref()
                    viewerChaptersRelay.accept(newChapters)
                }
    }

    override fun preloadChapter(chapter: ReaderChapter) {
        if (chapter.state != ReaderChapter.State.Wait && chapter.state !is ReaderChapter.State.Error) {
            return
        }

        val loader = loader ?: return

        loader.loadChapter(chapter)
                .observeOn(AndroidSchedulers.mainThread())
                // Update current chapters whenever a chapter is preloaded
                .doOnComplete { viewerChaptersRelay.value?.let(viewerChaptersRelay::accept) }
                .onErrorComplete()
                .subscribe()
                .also { rx { it } }
    }

    override fun onPageSelected(page: ReaderPage) {
        val currentChapters = viewerChaptersRelay.value ?: return

        val selectedChapter = page.chapter
        selectedChapter.chapter.last_page_read = page.index

        if (selectedChapter != currentChapters.currChapter) {
            onChapterChanged(currentChapters.currChapter, selectedChapter)
            loadNewChapter(selectedChapter)
        }
    }

    private fun loadNewChapter(chapter: ReaderChapter) {
        val loader = loader ?: return

        activeChapterDisposable?.dispose()
            activeChapterDisposable = getLoadObservable(loader, chapter)
                    .observeOn(AndroidSchedulers.mainThread())
                    .subscribe()
                    .also { rx { it } }
    }

    private fun onChapterChanged(fromChapter: ReaderChapter, toChapter: ReaderChapter) {
        saveChapterProgress(fromChapter)
        saveChapterHistory(fromChapter)
    }

    private fun saveChapterProgress(chapter: ReaderChapter) {
        chapterRepository.updateChapter(chapter.chapter)
    }

    private fun saveChapterHistory(chapter: ReaderChapter) {
    }

    override fun getCurrentChapter(): ReaderChapter? {
        return viewerChaptersRelay.value?.currChapter
    }

    override fun setMangaViewer(viewer: Int) {
        val manga = manga ?: return
        manga.viewer = viewer
        mangaRepository.updateManga(manga)
        rx {
            Observable.timer(250, TimeUnit.MILLISECONDS, AndroidSchedulers.mainThread())
                    .subscribe {
                        val currChapters = viewerChaptersRelay.value
                        if (currChapters != null) {
                            // Save current page
                            val currChapter = currChapters.currChapter
                            currChapter.requestedPage = currChapter.chapter.last_page_read

                            // Emit manga and chapters to the new viewer
                            view?.setManga(manga)
                            view?.setChapters(currChapters)
                        }
                    }
        }
    }

    override fun getMangaViewer(): Int {
        return manga?.viewer ?: 0
    }
}