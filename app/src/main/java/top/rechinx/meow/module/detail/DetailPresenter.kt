package top.rechinx.meow.module.detail

import android.content.Context
import io.reactivex.android.schedulers.AndroidSchedulers
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.network.Api
import top.rechinx.meow.source.Dmzj

class DetailPresenter(): BasePresenter<DetailView>() {

    private var mComic: Comic? = null
    private lateinit var mSourceManager: SourceManager

    override fun initSubscription() {

    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
    }

    fun load(source: Int, cid: String) {
        mComic = Comic(source, cid)
        mCompositeDisposable.add(Api.getComicInfo(mSourceManager.getParser(source)!!, mComic!!)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onComicLoadSuccess(mComic!!)
                    mView?.onChapterLoadSuccess(it)
                }, {
                    mView?.onComicLoadSuccess(mComic!!)
                    mView?.onParseError()
                }))
    }
}