package top.rechinx.meow.di

import android.provider.SyncStateContract
import androidx.room.Room
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import org.koin.experimental.builder.singleBy
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.data.catalog.CatalogRepositoryImpl
import top.rechinx.meow.data.chapter.ChapterRepositoryImpl
import top.rechinx.meow.data.manga.MangaRepositoryImpl
import top.rechinx.meow.domain.catalog.repository.CatalogRepository
import top.rechinx.meow.domain.chapter.repository.ChapterRepository
import top.rechinx.meow.domain.manga.repository.MangaRepository
import top.rechinx.meow.global.Constants

val repositoryModule = module {
    single {
        Room.databaseBuilder(androidApplication(), AppDatabase::class.java, Constants.DATABASE_NAME)
                .allowMainThreadQueries()
                .build()
    }

    single { get<AppDatabase>().mangaDao() }
    single { get<AppDatabase>().chapterDao() }

    singleBy<CatalogRepository, CatalogRepositoryImpl>()
    singleBy<MangaRepository, MangaRepositoryImpl>()
    singleBy<ChapterRepository, ChapterRepositoryImpl>()
}