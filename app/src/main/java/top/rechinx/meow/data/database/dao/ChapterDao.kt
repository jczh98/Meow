package top.rechinx.meow.data.database.dao

import android.arch.persistence.room.*
import io.reactivex.Maybe
import io.reactivex.Observable
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga

@Dao
interface ChapterDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertChapters(chapters: List<Chapter>)

    @Query("SELECT * FROM Chapter WHERE manga_id = :mangaId")
    fun getChapters(mangaId: Long) : Maybe<List<Chapter>>

    @Delete
    fun deleteChapters(list: List<Chapter>)
}