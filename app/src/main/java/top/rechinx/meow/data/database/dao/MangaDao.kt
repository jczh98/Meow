package top.rechinx.meow.data.database.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import io.reactivex.Maybe
import io.reactivex.Observable
import top.rechinx.meow.data.database.model.Manga

@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManga(manga: Manga)

    @Query("SELECT * FROM Manga WHERE sourceId = :sourceId AND cid = :cid")
    fun loadManga(sourceId: Long, cid: String): Manga?

    @Query("SELECT * FROM Manga WHERE id = :mangaId")
    fun loadManga(mangaId: Long): Maybe<Manga>
}