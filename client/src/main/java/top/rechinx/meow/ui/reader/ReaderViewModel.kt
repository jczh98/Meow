package top.rechinx.meow.ui.reader

import androidx.lifecycle.MutableLiveData
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.rxkotlin.addTo
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.domain.chapter.interactor.GetLocalChapters
import top.rechinx.meow.domain.manga.interactor.GetManga
import top.rechinx.meow.domain.manga.interactor.UpdateManga
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.page.interact.LoadChapter
import top.rechinx.meow.domain.page.model.ReaderChapter
import top.rechinx.meow.domain.page.model.ReaderPage
import top.rechinx.meow.domain.page.model.ViewerChapters
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel
import java.util.concurrent.TimeUnit

class ReaderViewModel @AssistedInject constructor(
        @Assisted private val params: ReaderViewModelParams,
        private val loadChapter: LoadChapter,
        private val getManga: GetManga,
        private val updateManga: UpdateManga,
        private val sourceManager: SourceManager,
        private val getLocalChapters: GetLocalChapters,
        private val preferences: PreferenceHelper,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    var manga: Manga? = null
        private set

    private var chapterId = params.chapterId

    private val actions = PublishRelay.create<ReaderAction>()

    val stateLiveData: MutableLiveData<ReaderViewState> = MutableLiveData()

    private val chapterList by lazy {
        val manga = manga!!
        val chapters = getLocalChapters.interact(manga).reversed()
        chapters.map(::ReaderChapter)
    }

    @AssistedInject.Factory
    interface Factory {
        fun create(params: ReaderViewModelParams): ReaderViewModel
    }

    init {
        actions.observeOn(schedulers.io)
                .reduxStore(
                        initialState = getInitialViewState(),
                        sideEffects = listOf(
                                ::mangaInitialized,
                                ::setChaptersSideEffects,
                                ::preloadChapterSideEffects,
                                ::setMangaViewerEffects
                        ),
                        reducer = { state, action -> action.reduce(state) }
                )
                .distinctUntilChanged()
                .observeOn(schedulers.main)
                .subscribe(stateLiveData::postValue)
                .addTo(disposables)
    }

    private fun getInitialViewState(): ReaderViewState = ReaderViewState()

    private fun mangaInitialized(
            actions: Observable<ReaderAction>,
            stateFn: StateAccessor<ReaderViewState>
    ) : Observable<ReaderAction> {
        return actions.ofType(ReaderAction.InitializeManga::class.java)
                .startWith(ReaderAction.InitializeManga)
                .filter {
                    val state = stateFn()
                    state.manga == null && params.mangaId != -1L
                            && params.chapterId != -1L
                }
                .switchMap { _ ->
                    getManga.interact(params.mangaId)
                            .doOnSubscribe {
                                Timber.w("Initializing manga")
                            }
                            .subscribeOn(schedulers.io)
                            .toObservable()
                            .flatMap {
                                this.manga = it
                                val source = sourceManager.getOrStub(it.source) as CatalogSource
                                Observable.just<ReaderAction>(
                                        ReaderAction.MangaInitialized(source, it)
                                )
                            }
                }
    }

    private fun setChaptersSideEffects(
            actions: Observable<ReaderAction>,
            stateFn: StateAccessor<ReaderViewState>
    ): Observable<ReaderAction> {
        return actions.ofType(ReaderAction.LoadChapter::class.java)
                .filter {
                    val state = stateFn()
                    state.source != null && state.manga != null
                }
                .switchMap {
                    val state = stateFn()
                    val source = state.source!!
                    val manga = state.manga!!
                    loadChapter.interact(manga, source, it.chapter, it.continued)
                            .andThen(Observable.fromCallable {
                                val chapterPos = chapterList.indexOf(it.chapter)
                                ViewerChapters(it.chapter,
                                        chapterList.getOrNull(chapterPos - 1),
                                        chapterList.getOrNull(chapterPos + 1))
                            })
                            .observeOn(schedulers.main)
                            .flatMap { newChapters ->
                                val oldChapters = stateLiveData.value?.chapters
                                newChapters.ref()
                                oldChapters?.unref()
                                Observable.just<ReaderAction>(ReaderAction.SetChapters(newChapters))
                            }
                }
    }

    private fun preloadChapterSideEffects(
            actions: Observable<ReaderAction>,
            stateFn: StateAccessor<ReaderViewState>
    ): Observable<ReaderAction> {
        return actions.ofType(ReaderAction.PreloadChapter::class.java)
                .filter {
                    val state = stateFn()
                    state.source != null && state.manga != null
                }
                .switchMap {
                    val state = stateFn()
                    val manga = state.manga!!
                    val source = state.source!!
                    loadChapter.interact(manga, source, it.chapter, false)
                            .doOnComplete { stateLiveData.value?.let(stateLiveData::postValue) }
                            .onErrorComplete()
                            .toObservable<ReaderAction>()
                            .flatMap {
                                Observable.just(ReaderAction.ChapterPreloaded)
                            }
                }
    }

    private fun setMangaViewerEffects(
            actions: Observable<ReaderAction>,
            stateFn: StateAccessor<ReaderViewState>
    ): Observable<ReaderAction> {
        return actions.ofType(ReaderAction.SetMangaViewer::class.java)
                .switchMap {
                    val state = stateFn()
                    val manga = state.manga!!
                    val currChapters = state.chapters!!
                    manga.viewer = it.viewer
                    Observable.timer(250, TimeUnit.MILLISECONDS, schedulers.main)
                            .concatMap {
                                updateManga.interact(manga)
                                        .observeOn(schedulers.main)
                                        .toObservable()
                            }
                            .flatMap {
                                val currChapter = currChapters.currChapter
                                currChapter.requestedPage = currChapter.chapter.progress
                                Observable.just<ReaderAction>(
                                        ReaderAction.MangaViewerChanged(manga, currChapters)
                                )
                            }
                }
    }

    fun preloadChapter(chapter: ReaderChapter) {
        if (chapter.state != ReaderChapter.State.Wait
                && chapter.state !is ReaderChapter.State.Error) {
            return
        }
        actions.accept(ReaderAction.PreloadChapter(chapter))
    }

    fun onPageSelected(page: ReaderPage) {
        val currentChapters = stateLiveData.value?.chapters ?: return

        val selectedChapter = page.chapter
        selectedChapter.chapter.progress = page.index

        if (selectedChapter != currentChapters.currChapter) {
            onChapterChanged(currentChapters.currChapter, selectedChapter)
            loadNewChapter(selectedChapter)
        }
    }

    fun getCurrentChapter(): ReaderChapter? {
        return stateLiveData.value?.chapters?.currChapter
    }

    fun setMangaViewer(viewer: Int) {
        actions.accept(ReaderAction.SetMangaViewer(viewer))
    }

    fun getMangaViewer(): Int {
        val viewer = stateLiveData.value?.viewer ?: return preferences.readerMode()
        return if(viewer == 0) preferences.readerMode() else viewer
    }

    fun loadInitialChapter() {
        val chapter = chapterList.first{ chapterId == it.chapter.id}
        loadNewChapter(chapter, params.isContinue)
    }

    private fun loadNewChapter(chapter: ReaderChapter, continued: Boolean = false) {
        Timber.d("Loading ${chapter.chapter.name}")

        actions.accept(ReaderAction.LoadChapter(chapter, continued))
    }

    private fun onChapterChanged(fromChapter: ReaderChapter, toChapter: ReaderChapter) {
        saveChapterProgress(fromChapter)
        saveChapterHistory(fromChapter)
        saveLastRead(toChapter)
    }

    private fun saveChapterProgress(chapter: ReaderChapter) {
    }

    private fun saveChapterHistory(chapter: ReaderChapter) {
    }

    fun saveLastRead(chapter: ReaderChapter?) {
    }

}
