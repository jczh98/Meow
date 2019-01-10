package top.rechinx.meow.di

import org.koin.androidx.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module
import top.rechinx.meow.ui.about.AboutViewModel
import top.rechinx.meow.ui.catalogbrowse.CatalogBrowseViewModel
import top.rechinx.meow.ui.catalogs.CatalogsViewModel
import top.rechinx.meow.ui.manga.MangaInfoViewModel

val viewModelModule = module {
    viewModel { AboutViewModel() }
    viewModel { CatalogsViewModel(get(), get()) }
    viewModel { (id: Long) -> CatalogBrowseViewModel(id, get(), get(), get()) }
    viewModel { MangaInfoViewModel(get(), get(), get(), get(), get()) }
}