package top.rechinx.meow.dao

import android.arch.persistence.room.Database
import android.arch.persistence.room.RoomDatabase
import android.arch.persistence.room.Room
import top.rechinx.meow.App
import top.rechinx.meow.global.Constants
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.Login


@Database(entities = [Comic::class, Login::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun comicDao(): ComicDao

    abstract fun loginDao(): LoginDao

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