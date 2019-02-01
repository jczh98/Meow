package top.rechinx.meow.ui.catalogbrowse

import androidx.lifecycle.MutableLiveData
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import com.squareup.inject.assisted.Assisted
import com.squareup.inject.assisted.AssistedInject
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.domain.manga.interactor.SearchMangasListFromSource
import top.rechinx.meow.domain.manga.model.MangasPage
import top.rechinx.meow.rikka.rx.Dispatcher
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel
import top.rechinx.meow.rikka.rx.scanWithPrevious
import top.rechinx.meow.ui.catalogbrowse.filters.FilterWrapper
import top.rechinx.meow.ui.catalogbrowse.filters.QueryMode

class CatalogBrowseViewModel @AssistedInject constructor(
        @Assisted private val params: CatalogBrowseParams,
        private val sourceManager: SourceManager,
        private val getMangasListFromSource: GetMangasListFromSource,
        private val searchMangasListFromSource: SearchMangasListFromSource,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    @AssistedInject.Factory
    interface Factory {
        fun create(params: CatalogBrowseParams): CatalogBrowseViewModel
    }

    private val actions = PublishRelay.create<CatalogBrowseAction>()

    val stateLiveData: MutableLiveData<Dispatcher<CatalogBrowseViewState>> = MutableLiveData()

    val source = sourceManager.getOrStub(params.sourceId) as CatalogSource

    init {
        actions
                .observeOn(schedulers.io)
                .reduxStore(
                        initialState = getInitialViewState(),
                        sideEffects = listOf(
                                ::loadNextSideEffect,
                                ::setFiltersSideEffect
                        ),
                        reducer = { state, action -> action.reduce(state) }
                )
                .distinctUntilChanged()
                .observeOn(schedulers.main)
                .scanWithPrevious()
                .subscribe(stateLiveData::postValue)
                .addTo(disposables)
    }

    private fun getInitialViewState(): CatalogBrowseViewState {
        return CatalogBrowseViewState(
                source = source,
                queryMode = QueryMode.Listing,
                filters = getWrappedFilters(source)
        )
    }

    private fun setFiltersSideEffect(
            actions: Observable<CatalogBrowseAction>,
            stateFn: StateAccessor<CatalogBrowseViewState>
    ) : Observable<CatalogBrowseAction> {
        return actions.ofType(CatalogBrowseAction.SetQueryMode.Filters::class.java)
                .flatMap { action ->
                    val filters = action.filters
                            .asSequence()
                            .onEach { it.updateInnerValue() }
                            .map { it.filter }
                            .toList()

                    if (filters.isEmpty()) {
                        Observable.empty()
                    } else {
                        val queryMode = QueryMode.FilterMode(filters)
                        Observable.just(CatalogBrowseAction.QueryModeUpdated(queryMode))
                    }
                }
    }

    private fun loadNextSideEffect(
            actions: Observable<CatalogBrowseAction>,
            stateFn: StateAccessor<CatalogBrowseViewState>
    ): Observable<CatalogBrowseAction> {
        return actions.filter { it is CatalogBrowseAction.LoadMore || it is CatalogBrowseAction.QueryModeUpdated }
                .startWith(CatalogBrowseAction.LoadMore) // Always load the initial page
                .filter {
                    val state = stateFn()
                    state.source != null  &&
                            !state.isLoading && state.hasMorePages
                }
                .switchMap {
                    val state = stateFn()
                    val source = state.source!!
                    val queryMode = state.queryMode!!
                    val nextPage = state.currentPage + 1

                    val mangaSinglePage = when (queryMode) {
                        is QueryMode.Listing -> {
                            getMangasListFromSource.interact(source, nextPage)
                        }
                        is QueryMode.FilterMode -> {
                            searchMangasListFromSource.interact(source, "", FilterList(queryMode.filters), nextPage)
                        }
                        is QueryMode.Search -> {
                            searchMangasListFromSource.interact(source, queryMode.query, FilterList(), nextPage)
                        }
                    }
                    mangaSinglePage
                            .doOnSubscribe { Timber.w("Requesting page $nextPage") }
                            .subscribeOn(schedulers.io)
                            .toObservable()
                            .map { pagedList ->
                                MangasPage(nextPage, pagedList.list, pagedList.hasNextPage)
                            }
                            .flatMap { mangasPage ->
                                Observable.just<CatalogBrowseAction>(CatalogBrowseAction.PageReceived(mangasPage))
                            }
                            .startWith(CatalogBrowseAction.Loading(true, state.currentPage))
                            .onErrorReturn(CatalogBrowseAction::LoadingError)
                }
    }

    /**
     * Returns the wrapped filters of the given [source]
     */
    private fun getWrappedFilters(source: CatalogSource): List<FilterWrapper<*>> {
        return source.getFilterList().map { filter ->
            FilterWrapper.from(filter)
        }
    }

    /**
     * Emits an action to request the next page of the catalog.
     */
    fun loadMore() {
        actions.accept(CatalogBrowseAction.LoadMore)
    }

    /**
     * Emits an action to query the page by give [filters]
     */
    fun setFilters(filters: List<FilterWrapper<*>>) {
        actions.accept(CatalogBrowseAction.SetQueryMode.Filters(filters))
    }

}
