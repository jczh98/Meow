package top.rechinx.meow.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.OnConflictStrategy
import android.arch.persistence.room.Query
import top.rechinx.meow.model.Source

@Dao
interface SourceDao {

    @Query("SELECT * FROM Source WHERE type = :type")
    fun load(type: Int): Source

    @Query("SELECT * FROM Source WHERE type = :type AND title = :title")
    fun identify(type: Int, title: String): Source?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg sources: Source)

}