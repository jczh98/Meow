package top.rechinx.meow.di

import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import top.rechinx.meow.ui.details.DetailViewModel
import top.rechinx.meow.ui.result.ResultViewModel

object ViewModelModule {

    val viewModelModule = module {
        viewModel { ResultViewModel() }
        viewModel { DetailViewModel(get()) }
    }
}