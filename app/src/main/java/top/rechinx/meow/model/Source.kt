package top.rechinx.meow.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

data class Source(
        var name: String,
        var title: String,
        var description: String,
        var isEnable: Boolean
)


