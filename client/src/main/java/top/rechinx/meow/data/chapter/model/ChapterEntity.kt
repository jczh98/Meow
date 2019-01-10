package top.rechinx.meow.data.chapter.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ChapterEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = -1,
        @ColumnInfo val mangaId: Long = -1,
        @ColumnInfo val key: String,
        @ColumnInfo val name: String,
        @ColumnInfo val progress: Int = 0, // last read page
        @ColumnInfo val date_upload: Long = 0,
        @ColumnInfo val number: Int = 0,
        @ColumnInfo val download: Boolean = false,
        @ColumnInfo val complete: Boolean = false
)