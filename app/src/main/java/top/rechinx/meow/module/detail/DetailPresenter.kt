package top.rechinx.meow.module.detail

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.network.Api
import top.rechinx.meow.source.Dmzj
import top.rechinx.meow.support.relog.ReLog

class DetailPresenter(): BasePresenter<DetailView>() {

    var mComic: Comic? = null

    private var page: Int = 1

    private lateinit var mSourceManager: SourceManager
    private lateinit var mComicManager: ComicManager

    override fun initSubscription() {

    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        mComicManager = ComicManager.getInstance()
    }

    fun load(source: Int, cid: String) {
        mComic = mComicManager.identify(source, cid)
        mCompositeDisposable.add(Api.getComicInfo(mSourceManager.getParser(source)!!, mComic!!, page++)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onComicLoadSuccess(mComic!!)
                    mView?.onChapterLoadSuccess(it)
                }, {
                    mView?.onComicLoadSuccess(mComic!!)
                    mView?.onParseError()
                }))
    }

    fun loadMore() {
        mCompositeDisposable.add(Api.getComicInfo(mSourceManager.getParser(mComic?.source!!)!!, mComic!!, page++)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onLoadMoreSuccess(it)
                }, {
                    mView?.onLoadMoreFailure()
                }))
    }

    fun refresh() {
        page = 1
        load(mComic?.source!!, mComic?.cid!!)
        mView?.onRefreshFinished()
    }

    fun favoriteComic() {
        mComic?.favorite = true
        mComicManager.updateOrInsert(mComic!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun unFavoriteComic() {
        mComic?.favorite = null
        mComicManager.updateOrInsert(mComic!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}