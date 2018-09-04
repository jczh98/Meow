package top.rechinx.meow.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey

@Entity
data class Source(
        @PrimaryKey(autoGenerate = true) var id: Long,
        @ColumnInfo var type: Int,
        @ColumnInfo var title: String
)


