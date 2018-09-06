package top.rechinx.meow.module.favorite

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.module.base.BasePresenter

class HistoryPresenter: BasePresenter<HistoryView>() {

    private lateinit var mComicManager: ComicManager

    override fun initSubscription() {
    }

    override fun onViewAttach() {
        mComicManager = ComicManager.getInstance()
    }

    fun load() {
        mComicManager.listHistory()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onComicLoadSuccess(it)
                }, {
                    mView?.onComicLoadFailure()
                })
    }
}