package top.rechinx.meow.di

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import top.rechinx.meow.ui.about.AboutViewModel

val viewModelModule = module {
    viewModel { AboutViewModel() }
}