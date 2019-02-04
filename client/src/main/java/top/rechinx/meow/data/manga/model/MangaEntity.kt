package top.rechinx.meow.data.manga.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import top.rechinx.meow.core.source.model.MangaInfo

@Entity
data class MangaEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        @ColumnInfo val source: Long,
        @ColumnInfo val key: String,
        @ColumnInfo val title: String,
        @ColumnInfo val artist: String = "",
        @ColumnInfo val author: String = "",
        @ColumnInfo val description: String = "",
        @ColumnInfo val genres: String = "",
        @ColumnInfo val status: Int = MangaInfo.UNKNOWN,
        @ColumnInfo val cover: String = "",
        @ColumnInfo val favorite: Boolean = false,
        @ColumnInfo val lastUpdate: Long = 0,
        @ColumnInfo val viewer: Int = 0,
        @ColumnInfo val last_chapter_key: String = "",
        @ColumnInfo val last_chapter_id: Long = 0,
        @ColumnInfo val viewed: Boolean = false,
        @ColumnInfo val downloaded: Boolean = false,
        @ColumnInfo val subscribed: Boolean = false
)