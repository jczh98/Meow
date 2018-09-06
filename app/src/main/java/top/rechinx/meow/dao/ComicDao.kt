package top.rechinx.meow.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import top.rechinx.meow.model.Comic

@Dao
interface ComicDao {

    @Insert
    fun insert(comic: Comic)

    @Update
    fun update(comic: Comic)

    @Query("SELECT * FROM Comic WHERE favorite = 1")
    fun listFavorite(): Flowable<List<Comic>>

    @Query("SELECT * FROM Comic WHERE id = :id")
    fun load(id: Long): Comic

    @Query("SELECT * FROM Comic WHERE source = :source AND cid = :cid")
    fun identify(source: Int, cid: String): Comic?

    @Query("SELECT * FROM Comic WHERE history = 1")
    fun listHistory(): Flowable<List<Comic>>
}
