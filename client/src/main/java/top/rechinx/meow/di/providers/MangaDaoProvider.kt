package top.rechinx.meow.di.providers

import toothpick.ProvidesSingletonInScope
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.data.manga.dao.MangaDao
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ProvidesSingletonInScope
internal class MangaDaoProvider @Inject constructor(
        private val database: AppDatabase
) : Provider<MangaDao> {

    override fun get(): MangaDao {
        return database.mangaDao()
    }

}