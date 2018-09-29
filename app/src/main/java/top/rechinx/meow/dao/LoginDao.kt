package top.rechinx.meow.dao

import android.arch.persistence.room.Dao
import android.arch.persistence.room.Insert
import android.arch.persistence.room.Query
import android.arch.persistence.room.Update
import top.rechinx.meow.model.Login

@Dao
interface LoginDao {

    @Insert
    fun insert(login: Login)

    @Update
    fun update(login: Login)

    @Query("SELECT isLogin FROM Login WHERE name = :name AND isLogin = 1")
    fun isLogin(name: String): Int?

    @Query("SELECT * FROM Login WHERE name = :name")
    fun identify(name: String): Login?

    @Query("SELECT auth FROM Login WHERE name = :name")
    fun auth(name: String): String?
}
