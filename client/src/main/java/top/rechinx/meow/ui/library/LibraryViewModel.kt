package top.rechinx.meow.ui.library

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.freeletics.rxredux.StateAccessor
import com.freeletics.rxredux.reduxStore
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.rxkotlin.addTo
import io.reactivex.rxkotlin.ofType
import timber.log.Timber
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.manga.interactor.GetSubscribedMangaList
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel
import javax.inject.Inject

class LibraryViewModel @Inject constructor(
        private val getSubscribedMangaList: GetSubscribedMangaList,
        private val sourceManager: SourceManager,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    private val actions = PublishRelay.create<LibraryAction>()

    val stateLiveData = MutableLiveData<LibraryViewState>()

    init {
        actions.observeOn(schedulers.io)
                .reduxStore(
                        initialState = getInitialViewState(),
                        sideEffects = listOf(
                                ::loadSubscribedMangaListSideEffect
                        ),
                        reducer = { state, action -> action.reduce(state) }
                )
                .distinctUntilChanged()
                .observeOn(schedulers.main)
                .subscribe(stateLiveData::postValue)
                .addTo(disposables)
    }

    private fun getInitialViewState() = LibraryViewState()

    private fun loadSubscribedMangaListSideEffect(
            actions: Observable<LibraryAction>,
            stateFn: StateAccessor<LibraryViewState>
    ): Observable<LibraryAction> {
        return actions.ofType<LibraryAction.LoadSubscribedMangaList>()
                .startWith(LibraryAction.LoadSubscribedMangaList)
                .switchMap { _ ->
                    getSubscribedMangaList.interact()
                            .doOnSubscribe {
                                Timber.w("Loading subscribed manga list")
                            }
                            .subscribeOn(schedulers.io)
                            .toObservable()
                            .flatMap { list ->
                                val pairList = list.map {
                                    it to sourceManager.getOrStub(it.source)
                                }
                                Observable.just(LibraryAction.SubscribedMangaLoaded(pairList))
                            }
                }
    }

}
