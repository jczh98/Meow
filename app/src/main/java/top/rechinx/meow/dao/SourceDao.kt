package top.rechinx.meow.dao

import android.arch.persistence.room.*
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import org.intellij.lang.annotations.Flow
import top.rechinx.meow.model.Source
import java.util.*

@Dao
interface SourceDao {

    @Query("SELECT * FROM Source WHERE type = :type")
    fun load(type: Int): Source

    @Query("SELECT * FROM Source WHERE type = :type AND title = :title")
    fun identify(type: Int, title: String): Source?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg sources: Source)

    @Update
    fun update(source: Source)

    @Query("SELECT * FROM Source")
    fun list(): Single<List<Source>>

    @Query("SELECT * FROM Source WHERE isEnable = 1")
    fun listEnable(): Single<List<Source>>
}