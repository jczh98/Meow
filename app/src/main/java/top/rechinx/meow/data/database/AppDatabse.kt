package top.rechinx.meow.data.database

import android.arch.persistence.room.Database
import android.arch.persistence.room.Room
import android.arch.persistence.room.RoomDatabase
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga

@Database(entities = [Manga::class, Chapter::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun mangaDao(): MangaDao

    abstract fun chapterDao(): ChapterDao

}