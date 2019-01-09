package top.rechinx.meow.data

import androidx.room.Database
import androidx.room.RoomDatabase
import top.rechinx.meow.data.manga.dao.MangaDao
import top.rechinx.meow.data.manga.model.MangaEntity

@Database(entities = [MangaEntity::class], version = 1)
abstract class AppDatabase : RoomDatabase(){

    abstract fun mangaDao() : MangaDao

}