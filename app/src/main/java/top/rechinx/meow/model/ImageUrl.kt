package top.rechinx.meow.model

import com.bumptech.glide.load.model.GlideUrl

data class ImageUrl(var id: Int,
                    var page_number: Int,
                    var chapterUrl: GlideUrl?,
                    var chapter: String?) {

    constructor(number: Int, chapterUrl: GlideUrl): this(0, number, chapterUrl, null)
}