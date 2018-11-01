package top.rechinx.meow.data.database.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import top.rechinx.meow.core.source.model.AbsManga

@Entity
data class Manga(@ColumnInfo override var cid: String?,
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
                 @ColumnInfo var sourceId: Long = 0,
                 @ColumnInfo var last_update: Long = 0,
                 @ColumnInfo var viewer: Int = 0): AbsManga() {

    constructor(): this(null, null, null, null, null, 0, null, false, null, null)

    @Ignore constructor(source: Long):this() {
        this.sourceId = source
    }

    @Ignore constructor(pathUrl: String, title: String, source: Long = 0): this() {
        this.sourceId = source
        this.title = title
        this.cid = pathUrl
    }

}