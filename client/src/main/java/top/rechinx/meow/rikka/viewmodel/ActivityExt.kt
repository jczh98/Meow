package top.rechinx.meow.rikka.viewmodel

import android.app.Activity
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Provider

inline fun <reified T : ViewModel> FragmentActivity.getViewModel(crossinline viewModel: () -> ViewModel) : T {
    val factory = object : ViewModelProvider.Factory {
        override fun <R : ViewModel?> create(modelClass: Class<R>): R {
            return viewModel.invoke() as R
        }
    }
    return ViewModelProviders.of(this, factory).get(T::class.java)
}

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> FragmentActivity.getViewModel(provider: Provider<T>): T {
    val factory = object : ViewModelProvider.Factory {
        override fun <R : ViewModel?> create(modelClass: Class<R>): R {
            return provider.get() as R
        }
    }
    return ViewModelProviders.of(this, factory).get(T::class.java)
}