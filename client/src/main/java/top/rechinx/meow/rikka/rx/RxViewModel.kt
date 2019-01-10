package top.rechinx.meow.rikka.rx

import androidx.lifecycle.ViewModel
import io.reactivex.disposables.CompositeDisposable

abstract class RxViewModel : ViewModel() {

    val disposables = CompositeDisposable()

    override fun onCleared() {
        super.onCleared()
        disposables.clear()
    }
}