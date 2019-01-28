package top.rechinx.meow.di.modules

import android.app.Application
import androidx.room.Room
import dagger.Module
import dagger.Provides
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.data.chapter.dao.ChapterDao
import top.rechinx.meow.data.manga.dao.MangaDao
import top.rechinx.meow.global.Constants
import javax.inject.Singleton

@Module
object DatabaseModule {

    @JvmStatic @Provides @Singleton
    fun provideRoomDatabase(
            application: Application
    ) : AppDatabase {
        return Room.databaseBuilder(application, AppDatabase::class.java, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
    }

    @JvmStatic @Provides @Singleton
    fun provideMangaDao(
            appDatabase: AppDatabase
    ) : MangaDao {
        return appDatabase.mangaDao()
    }


    @JvmStatic @Provides @Singleton
    fun provideChapterDao(
            appDatabase: AppDatabase
    ) : ChapterDao {
        return appDatabase.chapterDao()
    }

}