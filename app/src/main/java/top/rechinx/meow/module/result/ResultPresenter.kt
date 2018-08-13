package top.rechinx.meow.module.result

import android.content.Context
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import io.reactivex.android.schedulers.AndroidSchedulers
import top.rechinx.meow.App
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.network.Api
import top.rechinx.meow.source.Dmzj

class ResultPresenter(source: IntArray, keyword: String): BasePresenter<ResultView>() {

    private val STATE_NULL = 0
    private val STATE_DOING = 1
    private val STATE_DONE = 3

    private class State {
        var source: Int = 0
        var page: Int = 0
        var state: Int = 0
    }

    private lateinit var mStateArray: Array<State?>
    private lateinit var mSourceManager: SourceManager

    private var keyword: String = keyword
    private var error: Int = 0
    private var source: IntArray = source

    init {
        this.keyword = keyword
        if(source != null) {
            initStateArray(source)
        }
    }

    override fun initSubscription() {

    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        if(mStateArray == null) {
            initStateArray(loadSource())
        }
    }

    private fun initStateArray(source: IntArray) {
        mStateArray = arrayOfNulls<State>(source.size)
        for (i in mStateArray.indices) {
            mStateArray[i] = State()
            mStateArray[i]?.source  = source[i]
            mStateArray[i]?.page = 0
            mStateArray[i]?.state = STATE_NULL
        }
    }

    private fun loadSource(): IntArray {
        return arrayOf(1).toIntArray()
    }

    fun loadRefresh() {
        initStateArray(source)
        loadSearch(false)
    }

    fun loadSearch(isLoadMore: Boolean) {
        if(mStateArray.isEmpty()) {
            mView?.onSearchError()
            return
        }
        for(obj in mStateArray) {
            if(obj?.state == STATE_NULL) {
                val parser = mSourceManager.getParser(obj.source)
                obj.state = STATE_DOING
                mCompositeDisposable.add(Api.getSearchResult(parser!!, keyword, ++obj.page)
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe({
                            mView?.onSearchSuccess(it)
                            if(isLoadMore) mView?.onLoadMoreSuccess()
                        }, {
                            it.printStackTrace()
                            if(isLoadMore) mView?.onLoadMoreFailure()
                            if(obj.page == 1) {
                                obj.state = STATE_DONE
                                if(++error == mStateArray.size) {
                                    mView?.onSearchError()
                                }
                            }
                        }, {
                            obj.state = STATE_NULL
                        }))
            }
        }
    }
}