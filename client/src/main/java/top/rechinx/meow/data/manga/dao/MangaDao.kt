package top.rechinx.meow.data.manga.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import io.reactivex.Maybe
import top.rechinx.meow.data.manga.model.MangaEntity

@Dao
interface MangaDao {

    @Insert
    fun insertManga(mangaEntity: MangaEntity): Long

    @Query("SELECT * FROM MangaEntity WHERE `key` = :key AND source = :sourceId")
    fun queryManga(key: String, sourceId: Long): Maybe<MangaEntity>
}