package top.rechinx.meow.core

import com.bumptech.glide.load.model.GlideUrl
import okhttp3.Request
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.model.Source
import java.io.UnsupportedEncodingException

abstract class Parser {

    private var mTitle: String? = null

    @Throws(UnsupportedEncodingException::class)
    abstract fun getSearchRequest(keyword: String, page: Int): Request?

    abstract fun getInfoRequest(cid: String, page: Int): Request?

    @Throws(UnsupportedEncodingException::class)
    abstract fun parserInto(html: String, comic: Comic)

    abstract fun getSearchIterator(html: String, page: Int): SearchIterator?

    abstract fun getImageRequest(cid: String, image: String): Request?

    abstract fun parseImage(html: String): List<ImageUrl>?

    abstract fun parseChapter(html: String): List<Chapter>?

    abstract fun constructCoverGlideUrl(url: String): GlideUrl?

    abstract fun constructPicGlideUrl(url: String): GlideUrl?

    fun init(source: Source) {
        mTitle = source.title
    }

    fun getTitle(): String? = mTitle
}