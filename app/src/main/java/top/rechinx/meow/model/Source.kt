package top.rechinx.meow.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Source(
        @PrimaryKey(autoGenerate = true) val id: Long,
        @ColumnInfo val type: Int,
        @ColumnInfo val title: String
)


