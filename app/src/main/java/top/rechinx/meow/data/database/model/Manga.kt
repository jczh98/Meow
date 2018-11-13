package top.rechinx.meow.data.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Ignore
import androidx.room.PrimaryKey
import top.rechinx.meow.core.source.model.SManga

@Entity
data class Manga(@ColumnInfo override var url: String?,
                 @ColumnInfo override var title: String?,
                 @ColumnInfo override var author: String?,
                 @ColumnInfo override var description: String?,
                 @ColumnInfo override var genre: String?,
                 @ColumnInfo override var status: Int,
                 @ColumnInfo override var thumbnail_url: String?,
                 @ColumnInfo override var initialized: Boolean,
                 @ColumnInfo var sourceName: String?,
                 @ColumnInfo var last_chapter: String?,
                 @PrimaryKey(autoGenerate = true) var id: Long = 0,
                 @ColumnInfo var favorite: Boolean = false,
                 @ColumnInfo var last_read_chapter_id: Long = -1,
                 @ColumnInfo var sourceId: Long = 0,
                 @ColumnInfo var last_update: Long = 0,
                 @ColumnInfo var viewer: Int = 0,
                 @ColumnInfo var history: Boolean = false): SManga() {

    constructor(): this(null, null, null, null, null, 0, null, false, null, null)

    @Ignore constructor(source: Long):this() {
        this.sourceId = source
    }

    @Ignore constructor(pathUrl: String, title: String, source: Long = 0): this() {
        this.sourceId = source
        this.title = title
        this.url = pathUrl
    }

}