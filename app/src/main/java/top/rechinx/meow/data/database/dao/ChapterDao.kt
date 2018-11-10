package top.rechinx.meow.data.database.dao

import androidx.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga

@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChapters(chapters: List<Chapter>)

    @Insert
    fun insertChapter(chapter: Chapter): Long

    @Query("SELECT * FROM Chapter WHERE manga_id = :mangaId")
    fun getChapters(mangaId: Long) : Maybe<List<Chapter>>

    @Query("SELECT * FROM Chapter WHERE url = :chapterUrl AND manga_id = :mangaId")
    fun getChapter(chapterUrl: String, mangaId: Long) : Chapter?

    @Delete
    fun deleteChapters(list: List<Chapter>)

    @Update
    fun updateChapter(chapter: Chapter)
}