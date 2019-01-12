package top.rechinx.meow.ui.catalogbrowse

import android.view.ActionMode
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.BehaviorRelay
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.Observables
import io.reactivex.rxkotlin.addTo
import me.drakeet.multitype.Items
import timber.log.Timber
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.model.MangasPage
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.rx.Dispatcher
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel
import top.rechinx.meow.rikka.rx.scanWithPrevious
import javax.inject.Inject

class CatalogBrowseViewModel(
        private val params: CatalogBrowseParams,
        private val sourceManager: SourceManager,
        private val getMangasListFromSource: GetMangasListFromSource,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    private val actions = PublishRelay.create<CatalogBrowseAction>()

    val stateLiveData: MutableLiveData<Dispatcher<CatalogBrowseViewState>> = MutableLiveData()

    val source = sourceManager.getOrStub(params.sourceId) as CatalogSource

    init {
        actions
                .observeOn(schedulers.io)
                .reduxStore(
                        initialState = getInitialViewState(),
                        sideEffects = listOf(
                                ::loadNextSideEffect
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
                source = source
        )
    }

    private fun loadNextSideEffect(
            actions: Observable<CatalogBrowseAction>,
            stateFn: StateAccessor<CatalogBrowseViewState>
    ): Observable<CatalogBrowseAction> {
        return actions.filter { it is CatalogBrowseAction.LoadMore }
                .startWith(CatalogBrowseAction.LoadMore)
                .filter {
                    val state = stateFn()
                    state.source != null  &&
                            !state.isLoading && state.hasMorePages
                }
                .switchMap {
                    val state = stateFn()
                    val source = state.source!!
                    val nextPage = state.currentPage + 1
                    getMangasListFromSource.interact(source, nextPage)
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
     * Emits an action to request the next page of the catalog.
     */
    fun loadMore() {
        actions.accept(CatalogBrowseAction.LoadMore)
    }
}
