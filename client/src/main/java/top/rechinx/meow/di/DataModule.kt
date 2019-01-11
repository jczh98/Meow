package top.rechinx.meow.di

import toothpick.config.Module
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.data.catalog.CatalogRepositoryImpl
import top.rechinx.meow.data.chapter.ChapterRepositoryImpl
import top.rechinx.meow.data.chapter.dao.ChapterDao
import top.rechinx.meow.data.manga.MangaRepositoryImpl
import top.rechinx.meow.data.manga.dao.MangaDao
import top.rechinx.meow.di.providers.AppDatabaseProvider
import top.rechinx.meow.di.providers.ChapterDaoProvider
import top.rechinx.meow.di.providers.MangaDaoProvider
import top.rechinx.meow.domain.catalog.repository.CatalogRepository
import top.rechinx.meow.domain.chapter.repository.ChapterRepository
import top.rechinx.meow.domain.manga.repository.MangaRepository

object DataModule : Module() {

    init {
        bindProvider<AppDatabase, AppDatabaseProvider>()
        bindProvider<MangaDao, MangaDaoProvider>()
        bindProvider<ChapterDao, ChapterDaoProvider>()
        bindTo<CatalogRepository, CatalogRepositoryImpl>().singletonInScope()
        bindTo<MangaRepository, MangaRepositoryImpl>().singletonInScope()
        bindTo<ChapterRepository, ChapterRepositoryImpl>().singletonInScope()
    }

}