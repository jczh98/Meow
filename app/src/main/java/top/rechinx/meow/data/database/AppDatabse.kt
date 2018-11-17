package top.rechinx.meow.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.database.dao.TaskDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.database.model.Task

@Database(entities = [Manga::class, Chapter::class, Task::class], version = 1)
abstract class AppDatabase: RoomDatabase() {

    abstract fun mangaDao(): MangaDao

    abstract fun chapterDao(): ChapterDao

    abstract fun taskDao(): TaskDao
}