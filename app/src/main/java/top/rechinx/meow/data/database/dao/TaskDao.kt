package top.rechinx.meow.data.database.dao

import androidx.room.*
import io.reactivex.Flowable
import io.reactivex.Maybe
import io.reactivex.Observable
import top.rechinx.meow.data.database.model.Task

@Dao
interface TaskDao {

    @Insert
    fun insert(task: Task) : Long

    @Update
    fun update(task: Task)

    @Query("SELECT * FROM Task WHERE mangaId = :mangaId")
    fun list(mangaId: Long) : List<Task>

    @Query("SELECT * FROM Task WHERE mangaId = :mangaId")
    fun listInRx(mangaId: Long) : Maybe<List<Task>>

    @Query("DELETE FROM Task WHERE mangaId = :mangaId")
    fun deleteByMangaId(mangaId: Long)
}