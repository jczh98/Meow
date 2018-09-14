package top.rechinx.meow.module.source

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BasePresenter

class SourcePresenter: BasePresenter<SourceView>() {

    private lateinit var mSourceManager: SourceManager

    override fun initSubscription() {
    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
    }

    fun load() {
        mCompositeDisposable.add(mSourceManager.list()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onSourceLoadSuccess(it)
                },{
                    mView?.onSourceLoadFailure()
                }))
    }

    fun update(source: Source) {
        mSourceManager.update(source)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }
}