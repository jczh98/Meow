package top.rechinx.meow.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.Entity
import android.arch.persistence.room.RoomDatabase
import top.rechinx.meow.model.Source
import android.arch.persistence.room.Room
import android.os.Build
import top.rechinx.meow.App
import top.rechinx.meow.Constants
import top.rechinx.meow.model.Comic


@Database(entities = [Source::class, Comic::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun sourceDao(): SourceDao

    abstract fun comicDao(): ComicDao

    companion object {

        private var instance: AppDatabase ?= null

        fun getInstance(): AppDatabase {
            return instance ?: synchronized(this) {
                instance ?: Room
                        .databaseBuilder(App.instance, AppDatabase::class.java, Constants.DATABASE_NAME)
                        .allowMainThreadQueries()
                        .build()
                        .also { instance = it }
            }
        }
    }
}