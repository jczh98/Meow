package top.rechinx.meow.support.mvvm

import android.arch.lifecycle.ViewModel
import android.support.annotation.CallSuper
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

abstract class RxViewModel: ViewModel() {

    private val disposables = CompositeDisposable()

    fun rx(observer: () -> Disposable) {
        disposables.add(observer())
    }

    @CallSuper
    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}