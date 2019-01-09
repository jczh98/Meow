package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.domain.catalog.interactor.GetLocalCatalogs
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.domain.manga.interactor.GetOrAddManga

val interactorModule = module {
    single { GetLocalCatalogs(get()) }
    single { GetOrAddManga(get()) }
    single { GetMangasListFromSource(get()) }
}