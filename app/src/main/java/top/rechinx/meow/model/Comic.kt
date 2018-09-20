package top.rechinx.meow.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl

@Entity
data class Comic(@PrimaryKey(autoGenerate = true) var id: Long = 0,
                 @ColumnInfo var cid: String?,
                 @ColumnInfo var source: String?,
                 @ColumnInfo var sourceName: String?,
                 @ColumnInfo var image: String?,
                 @ColumnInfo var title: String?,
                 @ColumnInfo var author: String?,
                 @ColumnInfo var description: String?,
                 @ColumnInfo var status: String?,
                 @ColumnInfo var update: String?,
                 @ColumnInfo var favorite: Boolean?,
                 @ColumnInfo var last_chapter: String?,
                 @ColumnInfo var last_page: Int?,
                 @ColumnInfo var history: Boolean?,
                 @ColumnInfo var isPageReader: Boolean?,
                 @ColumnInfo var headers: String?) {

    constructor(): this(0, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)

    @Ignore constructor(cid: String, title: String?, image: String?, author: String?, update: String?): this(0, cid, null, null, image, title, author, null, null, update, null, null, null, null, null, null)

    @Ignore constructor(source: String, cid: String) : this(0, cid, source, null, null, null, null, null, null, null, null, null, null, null, null, null)

    fun setInfo(title: String?, cover: String?, update: String?, intro: String, author: String?, finish: Boolean, isPage: Boolean, headers: String?, sourceName: String?) {
        if (title != null) {
            this.title = title
        }
        if (cover != null) {
            this.image = cover
        }
        if (update != null) {
            this.update = update
        }
        this.description = intro
        if (author != null) {
            this.author = author
        }
        this.isPageReader = isPage
        this.headers = headers
        this.sourceName = sourceName
    }
}
