package top.rechinx.meow.ui.base

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import top.rechinx.meow.di.AppScope

class BaseViewModelProviderFactory(val scope: Any): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return AppScope.subscope(scope).getInstance(modelClass)
    }

}