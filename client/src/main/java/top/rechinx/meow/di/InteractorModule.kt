package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.domain.catalog.interactor.GetLocalCatalogs

val interactorModule = module {
    single { GetLocalCatalogs(get()) }
}