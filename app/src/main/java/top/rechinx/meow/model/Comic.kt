package top.rechinx.meow.model

import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl

data class Comic(var cid: String?,
                 var source: Int?,
                 var image: String?,
                 var title: String?,
                 var author: String?,
                 var description: String?,
                 var status: String?,
                 var update: String?,
                 var glideCover: GlideUrl?) {

    constructor(source: Int, cid: String) : this(source, cid, null, null, null) {
        this.cid = cid
        this.source = source
    }
    constructor(source: Int, cid: String, title: String?, image: String?, author: String?) :
            this(cid, source, image, title, author, null, null, null, null) {
        this.cid = cid
        this.source = source
        this.title = title
        this.image = image
        this.author = author
    }

    constructor(source: Int, cid: String, title: String, image: String, author: String, update: String, glideCover: GlideUrl?) :
            this(cid, source, image, title, author, null, null, null, glideCover) {
        this.cid = cid
        this.source = source
        this.title = title
        this.image = image
        this.author = author
        this.update = update
    }

    fun setInfo(title: String?, cover: String?, update: String?, intro: String, author: String?, finish: Boolean, glideCover: GlideUrl?) {
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
        this.glideCover = glideCover
    }
}
