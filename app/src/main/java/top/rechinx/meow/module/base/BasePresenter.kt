package top.rechinx.meow.module.base

import io.reactivex.disposables.CompositeDisposable

abstract class BasePresenter <T: BaseView> {

    protected var mView: T? = null
    protected lateinit var mCompositeDisposable: CompositeDisposable

    fun attachView(view: T) {
        this.mView = view
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
        mView = null
    }
}