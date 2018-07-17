package top.rechinx.meow.core

import okhttp3.Request
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import java.io.UnsupportedEncodingException

interface Parser {

    @Throws(UnsupportedEncodingException::class)
    fun getSearchRequest(keyword: String, page: Int): Request?

    fun getInfoRequest(cid: String): Request?

    @Throws(UnsupportedEncodingException::class)
    fun parserInto(html: String): Comic?

    fun getSearchIterator(html: String, page: Int): SearchIterator?

    fun getImageRequest(cid: String, image: String): Request?

    fun parseImage(html: String): List<ImageUrl>
}