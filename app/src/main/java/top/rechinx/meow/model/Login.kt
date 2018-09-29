package top.rechinx.meow.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Login(
        @PrimaryKey var name: String,
        @ColumnInfo var username: String?,
        @ColumnInfo var password: String?,
        @ColumnInfo var auth: String?,
        @ColumnInfo var isLogin: Boolean?
)