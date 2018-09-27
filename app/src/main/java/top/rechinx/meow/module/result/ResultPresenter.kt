package top.rechinx.meow.module.result

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import top.rechinx.meow.App
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BasePresenter
import io.reactivex.functions.Function

class ResultPresenter(keyword: String): BasePresenter<ResultView>() {

    private val STATE_NULL = 0
    private val STATE_DOING = 1
    private val STATE_DONE = 3

    private class State {
        var source: String? = null
        var page: Int = 0
        var state: Int = 0
    }

    private lateinit var mStateArray: ArrayList<State?>
    private lateinit var mSourceManager: SourceManager

    private var keyword: String = keyword
    private var error: Int = 0

    init {
        this.keyword = keyword
    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        initStateArray()
    }

    private fun initStateArray() {
        mStateArray = ArrayList()
        val list = SourceManager.getInstance().getSourceNames()
        for(item in list) {
            if(!App.instance.getPreferenceManager().getBoolean(item, true)) {
                continue
            }
            var state = State()
            state.state = STATE_NULL
            state.source = item
            state.page = 0
            mStateArray.add(state)
        }
    }

    fun loadRefresh() {
        initStateArray()
        loadSearch(false)
    }

    fun loadSearch(isLoadMore: Boolean) {
        if(mStateArray.isEmpty()) {
            mView?.onSearchError()
            return
        }
        for(obj in mStateArray) {
            if(obj?.state == STATE_NULL) {
                obj.state = STATE_DOING
                mCompositeDisposable.add(SourceManager.getInstance().rxGetSource(obj.source!!)
                        .flatMap(Function<SaSource, Observable<Comic>> {
                            return@Function it.getSearchResult(keyword, obj.page++)
                        })
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe( {
                            mView?.onSearchSuccess(it)
                            if(isLoadMore) mView?.onLoadMoreSuccess()
                        }, {
                            it.printStackTrace()
                            if(isLoadMore) mView?.onLoadMoreFailure()
                            if(obj.page == 1) {
                                obj.state = STATE_DONE
                            }
                            if(++error == mStateArray.size) {
                                mView?.onSearchError()
                            }
                        }, {
                            obj.state = STATE_NULL
                        })
                )
            }
        }
    }
}