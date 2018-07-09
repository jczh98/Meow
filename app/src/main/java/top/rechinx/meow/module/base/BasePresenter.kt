package top.rechinx.meow.module.base

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter <T: BaseView> {

    protected var mBaseView: T? = null
    protected lateinit var mCompositeDisposable: CompositeDisposable

    fun attachView(view: T) {
        this.mBaseView = view
        onViewAttach()
        mCompositeDisposable = CompositeDisposable()
        initSubscription()
    }

    protected abstract fun initSubscription()

    protected abstract fun onViewAttach()

    public fun detachView() {
        if(mCompositeDisposable != null) {
            mCompositeDisposable.dispose()
        }
        mBaseView = null
    }
}