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
                 @ColumnInfo var source: Int?,
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
                 @Ignore var glideCover: GlideUrl?) {

    constructor(): this(0, null, null, null, null, null, null, null, null, null, null, null, null, null, null)

    @Ignore constructor(source: Int, cid: String) : this(source, cid, null, null, null) {
        this.cid = cid
        this.source = source
    }

    @Ignore constructor(source: Int, cid: String, title: String?, image: String?, author: String?) :
            this(0, cid, source, image, title, author, null, null, null, null, null, null, null, null, null) {
        this.cid = cid
        this.source = source
        this.title = title
        this.image = image
        this.author = author
    }

    @Ignore constructor(source: Int, cid: String, title: String, image: String, author: String, update: String, glideCover: GlideUrl?) :
            this(0, cid, source, image, title, author, null, null, null, null, null, null, null, null, glideCover) {
        this.cid = cid
        this.source = source
        this.title = title
        this.image = image
        this.author = author
        this.update = update
    }

    fun setInfo(title: String?, cover: String?, update: String?, intro: String, author: String?, finish: Boolean, isPage: Boolean, glideCover: GlideUrl?) {
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
        this.glideCover = glideCover
    }
}
