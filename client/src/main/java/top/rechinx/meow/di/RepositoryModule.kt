package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.data.catalog.CatalogRepositoryImpl
import top.rechinx.meow.domain.catalog.repository.CatalogRepository

val repositoryModule = module {
    single { CatalogRepositoryImpl(get()) as CatalogRepository }
}