package top.rechinx.meow.data.chapter.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import io.reactivex.Maybe
import top.rechinx.meow.data.chapter.model.ChapterEntity

@Dao
interface ChapterDao {

    @Insert
    fun insertChapter(chapterEntity: ChapterEntity): Long

    @Update
    fun updateChapter(chapterEntity: ChapterEntity)

    @Query("SELECT * FROM ChapterEntity WHERE `key` = :key AND mangaId = :mangaId")
    fun queryChapter(key: String, mangaId: Long): Maybe<ChapterEntity>

    @Query("SELECT * FROM ChapterEntity WHERE id = :id")
    fun queryChapter(id: Long): Maybe<ChapterEntity>

}