package top.rechinx.meow.di

import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import top.rechinx.meow.data.database.AppDatabase
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.repository.ChapterRepository
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.global.Constants

object DatabaseModule {

    val databseModule = module(createOnStart = true) {
        single {
            Room.databaseBuilder(androidApplication(), AppDatabase::class.java, Constants.DATABASE_NAME)
                    .allowMainThreadQueries()
                    .build()
        }
        single { get<AppDatabase>().mangaDao() }
        single { get<AppDatabase>().chapterDao() }
        single { MangaRepository(get(), get(), get()) }
        single { ChapterRepository(get(), get()) }
    }
}