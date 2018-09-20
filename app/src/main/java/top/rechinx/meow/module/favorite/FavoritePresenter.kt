package top.rechinx.meow.module.favorite

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.module.base.BasePresenter

class FavoritePresenter: BasePresenter<FavoriteView>() {

    private lateinit var mComicManager: ComicManager

    override fun onViewAttach() {
        mComicManager = ComicManager.getInstance()
    }

    fun load() {
        mComicManager.listFavorite()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onComicLoadSuccess(it)
                }, {
                    mView?.onComicLoadFailure()
                })
    }
}