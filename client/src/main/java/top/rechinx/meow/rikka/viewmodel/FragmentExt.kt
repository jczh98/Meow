package top.rechinx.meow.rikka.viewmodel

import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import javax.inject.Provider

@Suppress("UNCHECKED_CAST")
inline fun <reified T : ViewModel> Fragment.getSharedViewModel(provider: Provider<T>): T {
    val factory = object : ViewModelProvider.Factory {
        override fun <R : ViewModel?> create(modelClass: Class<R>): R {
            return provider.get() as R
        }
    }
    return if (activity != null) {
        ViewModelProviders.of(activity!!, factory).get(T::class.java)
    } else {
        error("Activity is null")
    }
}