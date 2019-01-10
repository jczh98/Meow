package top.rechinx.meow.data

import androidx.room.Database
import androidx.room.RoomDatabase
import top.rechinx.meow.data.chapter.dao.ChapterDao
import top.rechinx.meow.data.chapter.model.ChapterEntity
import top.rechinx.meow.data.manga.dao.MangaDao
import top.rechinx.meow.data.manga.model.MangaEntity

@Database(entities = [MangaEntity::class, ChapterEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun mangaDao() : MangaDao

    abstract fun chapterDao() : ChapterDao
}