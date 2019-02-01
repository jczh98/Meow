package top.rechinx.meow.ui.manga

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.chapter.interactor.FetchChaptersFromSource
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.chapter.model.ChaptersPage
import top.rechinx.meow.domain.manga.interactor.GetManga
import top.rechinx.meow.domain.manga.interactor.UpdateManga
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.rx.Dispatcher
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel
import top.rechinx.meow.rikka.rx.scanWithPrevious
import javax.inject.Inject

class MangaInfoViewModel @AssistedInject constructor(
        @Assisted private val mangaId: Long,
        private val getManga: GetManga,
        private val updateManga: UpdateManga,
        private val fetchChaptersFromSource: FetchChaptersFromSource,
        private val sourceManager: SourceManager,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(mangaId: Long): MangaInfoViewModel
    }

    private val actions = PublishRelay.create<MangaInfoAction>()

    val stateLiveData = MutableLiveData<Dispatcher<MangaInfoViewState>>()

    init {
        actions.observeOn(schedulers.io)
                .reduxStore(
                        initialState = getInitialViewState(),
                        sideEffects = listOf(
                                ::mangaInitialized,
                                ::loadChaptersSideEffect
                        ),
                        reducer = { state, action -> action.reduce(state) }
                )
                .distinctUntilChanged()
                .observeOn(schedulers.main)
                .scanWithPrevious()
                .subscribe(stateLiveData::postValue)
                .addTo(disposables)
    }

    private fun getInitialViewState(): MangaInfoViewState {
        return MangaInfoViewState()
    }

    private fun mangaInitialized(
            actions: Observable<MangaInfoAction>,
            stateFn: StateAccessor<MangaInfoViewState>
    ) : Observable<MangaInfoAction> {
        return actions.filter { it is MangaInfoAction.InitializeManga }
                .startWith(MangaInfoAction.InitializeManga)
                .filter {
                    val state = stateFn()
                    state.manga == null && !state.isLoading
                }.switchMap { _ ->
                    getManga.interact(mangaId)
                            .doOnSubscribe {
                                Timber.w("Initializing manga")
                            }
                            .subscribeOn(schedulers.io)
                            .concatMap {
                                updateManga.interact(it)
                            }
                            .toObservable()
                            .flatMap {
                                val source = sourceManager.getOrStub(it.source) as CatalogSource
                                Observable.just<MangaInfoAction>(
                                        MangaInfoAction.MangaInitialized(source, it)
                                )
                            }
                            .onErrorReturn(MangaInfoAction::LoadingError)
                }
                .doOnNext {
                    loadChapters()
                }
    }

    private fun loadChaptersSideEffect(
            actions: Observable<MangaInfoAction>,
            stateFn: StateAccessor<MangaInfoViewState>
    ) : Observable<MangaInfoAction> {
        return actions.filter { it is MangaInfoAction.LoadChapters }
                .filter {
                    val state = stateFn()
                    state.manga != null &&
                            !state.isLoading && state.hasNextPage
                }
                .switchMap { _ ->
                    val state = stateFn()
                    val source = state.source!!
                    val manga = state.manga!!
                    val nextPage = state.currentPage + 1
                    fetchChaptersFromSource.interact(source, manga, nextPage)
                            .doOnSubscribe {
                                Timber.w("Requesting $nextPage")
                            }
                            .subscribeOn(schedulers.io)
                            .toObservable()
                            .flatMap {
                                Observable.just<MangaInfoAction>(
                                        MangaInfoAction.PageReceived(ChaptersPage(
                                        nextPage,
                                        it.list,
                                        it.hasNextPage
                                )))
                            }
                            .startWith(MangaInfoAction.Loading(true))
                            .onErrorReturn(MangaInfoAction::LoadingError)
                }
    }

    fun loadChapters() {
        actions.accept(MangaInfoAction.LoadChapters)
    }

}
