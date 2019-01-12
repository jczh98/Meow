package top.rechinx.meow.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment
import toothpick.Scope
import toothpick.Toothpick
import toothpick.config.Module
import top.rechinx.meow.di.AppScope

abstract class BaseFragment : Fragment() {

    val scope: Scope =  AppScope.subscope(this).also { scope ->
        getModule()?.let { scope.installModules(it) }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Toothpick.inject(this, scope)
    }

    abstract fun getModule(): Module?

    override fun onDestroy() {
        super.onDestroy()
        Toothpick.closeScope(this)
    }

}