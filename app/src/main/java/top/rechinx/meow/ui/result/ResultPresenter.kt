package top.rechinx.meow.ui.result

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.R
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.mvp.RxPresenter
import java.lang.Exception
import java.util.*

class ResultPresenter(private val sourceManager: SourceManager): ResultContract.Presenter, RxPresenter<ResultContract.View>() {

    private var stateArray: ArrayList<State> = ArrayList()

    var error = 0

    init {
        initStateArray()
    }

    private fun initStateArray() {
        stateArray = ArrayList()
        for(source in sourceManager.getSources()) {
            val state = State()
            state.state = State.STATE_NULL
            state.source = source
            state.page = 0
            stateArray.add(state)
        }
    }

    override fun refresh(keyword: String) {
        error = 0
        initStateArray()
        search(keyword, false)
    }

    override fun search(keyword: String, isLoadMore: Boolean) {
        for(obj in stateArray) {
            val source = obj.source
            if(source != null) {
                if(obj.state == State.STATE_NULL) {
                    obj.state = State.STATE_DOING
                    rx {
                        source.fetchSearchManga(keyword, obj.page++, FilterList())
                                .flatMap {
                                    return@flatMap Observable.create(ObservableOnSubscribe<Manga> { emitter ->
                                        try {
                                            if(it.mangas.isEmpty()) throw Exception()
                                            for(item in it.mangas) {
                                                val manga = Manga()
                                                manga.copyFrom(item)
                                                manga.sourceId = source.id
                                                manga.sourceName = source.name
                                                emitter.onNext(manga)
                                                Thread.sleep(Random().nextInt(200).toLong())
                                            }
                                            emitter.onComplete()
                                        } catch (e: Exception) {
                                            emitter.onError(e)
                                        }
                                    })
                                }
                                .subscribeOn(Schedulers.io())
                                .observeOn(AndroidSchedulers.mainThread())
                                .subscribe({
                                    view?.onMangaLoadCompleted(it)
                                    if(isLoadMore) view?.onLoadMoreCompleted()
                                }, {
                                    it.printStackTrace()
                                    if(isLoadMore) view?.onLoadMoreCompleted()
                                    if(obj.page == 1) {
                                        obj.state = State.STATE_DONE
                                    }
                                    if(++error == stateArray.size) {
                                        view?.onLoadError()
                                    }
                                }, {
                                    obj.state = State.STATE_NULL
                                })
                    }
                }
            }
        }
    }

    private class State {
        var source: Source? = null
        var page: Int = 0
        var state: Int = STATE_NULL

        companion object {
            const val STATE_NULL = 0
            const val STATE_DOING = 1
            const val STATE_DONE = 3
        }
    }
}