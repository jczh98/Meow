package top.rechinx.meow.module.detail

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.network.Api
import top.rechinx.meow.support.relog.ReLog
import java.util.*

class DetailPresenter: BasePresenter<DetailView>() {

    var mComic: Comic? = null

    private var page: Int = 0

    private lateinit var mSourceManager: SourceManager
    private lateinit var mComicManager: ComicManager

    override fun initSubscription() {

    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        mComicManager = ComicManager.getInstance()
    }

    fun load(source: String, cid: String) {
        mComic = mComicManager.identify(source, cid)
        mCompositeDisposable.add(SourceManager.getInstance().rxGetSource(source)
                .flatMap(Function<SaSource, Observable<List<Chapter>>> {
                    return@Function it.getComicInfo(mComic!!, page++)
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onComicLoadSuccess(mComic!!)
                    mView?.onChapterLoadSuccess(it)
                }, {
                    mView?.onComicLoadSuccess(mComic!!)
                    mView?.onParseError()
                }))
    }

    fun loadMore() {
        mCompositeDisposable.add(SourceManager.getInstance().rxGetSource(mComic?.source!!)
                .flatMap(Function<SaSource, Observable<List<Chapter>>> {
                    return@Function it.getComicInfo(mComic!!, page++)
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onLoadMoreSuccess(it)
                }, {
                    mView?.onLoadMoreFailure()
                }))
    }

    fun refresh() {
        page = 0
        load(mComic?.source!!, mComic?.cid!!)
        mView?.onRefreshFinished()
    }

    fun favoriteComic() {
        mComic?.favorite = true
        mCompositeDisposable.add(mComicManager.updateOrInsert(mComic!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{mComic = mComicManager.identify(mComic?.source!!, mComic?.cid!!)})

    }

    fun unFavoriteComic() {
        mComic?.favorite = null
        mCompositeDisposable.add(mComicManager.updateOrInsert(mComic!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{mComic = mComicManager.identify(mComic?.source!!, mComic?.cid!!)})
  }

    fun updateComic() {
        mCompositeDisposable.add(mComicManager.updateOrInsert(mComic!!)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe{mComic = mComicManager.identify(mComic?.source!!, mComic?.cid!!)})
    }

}