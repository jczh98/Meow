package top.rechinx.meow.di.providers

import toothpick.ProvidesSingletonInScope
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.data.chapter.dao.ChapterDao
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ProvidesSingletonInScope
internal class ChapterDaoProvider @Inject constructor(
        private val appDatabase: AppDatabase
) : Provider<ChapterDao> {

    override fun get(): ChapterDao {
        return appDatabase.chapterDao()
    }

}