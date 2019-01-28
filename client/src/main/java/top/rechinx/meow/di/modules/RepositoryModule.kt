package top.rechinx.meow.di.modules

import dagger.Binds
import dagger.Module
import top.rechinx.meow.data.catalog.CatalogRepositoryImpl
import top.rechinx.meow.data.chapter.ChapterRepositoryImpl
import top.rechinx.meow.data.manga.MangaRepositoryImpl
import top.rechinx.meow.domain.catalog.repository.CatalogRepository
import top.rechinx.meow.domain.chapter.repository.ChapterRepository
import top.rechinx.meow.domain.manga.repository.MangaRepository

@Module
abstract class RepositoryModule {

    @Binds abstract fun mangaRepository(impl: MangaRepositoryImpl): MangaRepository

    @Binds abstract fun chapterRepository(impl: ChapterRepositoryImpl): ChapterRepository

    @Binds abstract fun catalogRepository(impl: CatalogRepositoryImpl): CatalogRepository
}