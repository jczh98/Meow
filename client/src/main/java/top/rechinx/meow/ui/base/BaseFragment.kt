package top.rechinx.meow.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProviders
import timber.log.Timber
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import top.rechinx.meow.di.AppScope
import top.rechinx.meow.di.bindInstance

abstract class BaseFragment : Fragment() {

    lateinit var scope: Scope

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        scope = AppScope.subscope(this).also { scope ->
            getModule()?.let { scope.installModules(it) }
        }
    }

    abstract fun getModule(): Module?

    override fun onDestroy() {
        super.onDestroy()
        Toothpick.closeScope(this)
    }

}

inline fun <reified T : ViewModel> BaseFragment.getViewModel(): T {
    val factory = BaseViewModelProviderFactory(this)
    return ViewModelProviders.of(this, factory).get(T::class.java)
}

inline fun <reified T : ViewModel> BaseFragment.getSharedViewModel(): T {
    val factory = BaseViewModelProviderFactory(this)
    return if (activity != null) {
        ViewModelProviders.of(activity!!, factory).get(T::class.java)
    } else {
        error("Activity is null")
    }
}