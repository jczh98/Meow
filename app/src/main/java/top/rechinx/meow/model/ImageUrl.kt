package top.rechinx.meow.model

import com.bumptech.glide.load.model.GlideUrl

data class ImageUrl(var id: Int,
                    var page_number: Int,
                    var imageUrl: String?,
                    var chapterUrl: GlideUrl?,
                    var chapter: String?,
                    var headers: String?) {

    constructor(number: Int, imageUrl: String?, chapter: String?, headers: String?): this(0, number, imageUrl, null, chapter, headers)

    constructor(number: Int, chapterUrl: GlideUrl): this(0, number, null, chapterUrl, null, null)
}