package top.rechinx.meow.support.mvp

import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxPresenter<V>: BasePresenter<V> {

    private val disposable = CompositeDisposable()

    override var view: V? = null

    fun rx(job: () -> Disposable) {
        disposable.add(job())
    }

    override fun subscribe(view: V) {
        this.view = view
    }

    @CallSuper
    override fun unsubscribe() {
        disposable.dispose()
        view = null
    }
}