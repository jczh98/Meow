package top.rechinx.meow.model

data class Comic(var cid: String?,
                 var source: Int?,
                 var image: String?,
                 var title: String?,
                 var author: String?,
                 var description: String?,
                 var status: String?,
                 var update: String?) {

    constructor(source: Int, cid: String) : this(source, cid, null, null, null) {
        this.cid = cid
        this.source = source
    }
    constructor(source: Int, cid: String, title: String?, image: String?, author: String?) :
            this(cid, source, image, title, author, null, null, null) {
        this.cid = cid
        this.source = source
        this.title = title
        this.image = image
        this.author = author
    }

    fun setInfo(title: String?, cover: String?, update: String?, intro: String, author: String?, finish: Boolean) {
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
    }
}
