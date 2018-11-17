package top.rechinx.meow.data.database.dao

import androidx.room.*
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

    @Query("SELECT * FROM Manga WHERE sourceId = :sourceId AND url = :url")
    fun loadManga(sourceId: Long, url: String): Manga?

    @Query("SELECT * FROM Manga WHERE id = :mangaId")
    fun load(mangaId: Long): Manga?

    @Query("SELECT * FROM Manga WHERE id = :mangaId")
    fun loadManga(mangaId: Long): Maybe<Manga>

    @Query("SELECT * FROM Manga")
    fun listMangas(): Flowable<List<Manga>>

    @Query("SELECT * FROM Manga WHERE favorite = 1")
    fun listFavorite(): Flowable<List<Manga>>

    @Query("SELECT * FROM Manga WHERE history = 1")
    fun listHistory(): Flowable<List<Manga>>

    @Query("SELECT * FROM Manga WHERE download = 1")
    fun listDownload(): Flowable<List<Manga>>

    @Query("SELECT * FROM Manga WHERE sourceId = :sourceId AND url = :url")
    fun relayManga(sourceId: Long, url: String): Flowable<Manga>
}