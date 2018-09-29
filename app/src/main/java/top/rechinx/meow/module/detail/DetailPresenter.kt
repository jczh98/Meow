package top.rechinx.meow.module.detail

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.manager.LoginManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BasePresenter

class DetailPresenter: BasePresenter<DetailView>() {

    var mComic: Comic? = null

    private var page: Int = 0

    private lateinit var mSourceManager: SourceManager
    private lateinit var mComicManager: ComicManager
    private lateinit var mLoginManager: LoginManager

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        mComicManager = ComicManager.getInstance()
        mLoginManager = LoginManager.getInstance()
    }

    fun load(source: String, cid: String) {
        mComic = mComicManager.identify(source, cid)
        mCompositeDisposable.add(SourceManager.getInstance().rxGetSource(source)
                .flatMap(Function<SaSource, Observable<List<Chapter>>> {
                    if(mLoginManager.isLogin(it.name)) it.setLogin(mLoginManager.getAuth(it.name))
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
                    if(mLoginManager.isLogin(it.name)) it.setLogin(mLoginManager.getAuth(it.name))
                    return@Function it.getComicInfo(mComic!!, page++)
                }).observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onLoadMoreSuccess(it)
                }, {
                    it.printStackTrace()
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