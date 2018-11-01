package top.rechinx.meow.data.database.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import top.rechinx.meow.data.database.model.Manga

@Dao
interface MangaDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertManga(manga: Manga)

    @Update
    fun updateManga(manga: Manga)

    @Query("SELECT * FROM Manga WHERE sourceId = :sourceId AND cid = :cid")
    fun loadManga(sourceId: Long, cid: String): Manga?

    @Query("SELECT * FROM Manga WHERE id = :mangaId")
    fun loadManga(mangaId: Long): Maybe<Manga>

    @Query("SELECT * FROM Manga WHERE favorite = 1")
    fun listFavorite(): Flowable<List<Manga>>

    @Query("SELECT * FROM Manga WHERE history = 1")
    fun listHistory(): Flowable<List<Manga>>
}