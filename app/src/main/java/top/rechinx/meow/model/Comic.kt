package top.rechinx.meow.model

data class Comic(var cid: String,
                 var source: Int,
                 var image: String,
                 var title: String,
                 var author: String,
                 var description: String?,
                 var status: String?,
                 var update: String?) {

    constructor(source: Int, cid: String, title: String, image: String, author: String) :
            this(cid, source, image, title, author, null, null, null) {
        this.cid = cid
        this.source = source
        this.title = title
        this.image = image
        this.author = author
    }
}
