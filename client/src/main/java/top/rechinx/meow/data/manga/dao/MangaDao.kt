package top.rechinx.meow.data.manga.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Flowable
import io.reactivex.Maybe
import top.rechinx.meow.data.manga.model.MangaEntity

@Dao
interface MangaDao {

    @Insert
    fun insertManga(mangaEntity: MangaEntity): Long

    @Update
    fun updateManga(mangaEntity: MangaEntity)

    @Query("SELECT * FROM MangaEntity WHERE `key` = :key AND source = :sourceId")
    fun queryManga(key: String, sourceId: Long): Maybe<MangaEntity>

    @Query("SELECT * FROM MangaEntity WHERE id = :id")
    fun queryManga(id: Long): Maybe<MangaEntity>

    @Query("SELECT * FROM MangaEntity WHERE subscribed = 1")
    fun querySubscribedMangaList(): Flowable<List<MangaEntity>>
}