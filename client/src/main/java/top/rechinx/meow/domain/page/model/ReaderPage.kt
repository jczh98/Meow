package top.rechinx.meow.domain.page.model

import top.rechinx.meow.core.source.model.PageInfo
import java.io.InputStream

class ReaderPage(index: Int,
                 url: String,
                 imageUrl: String?,
                 var stream: (() -> InputStream)? = null): PageInfo(index, url, imageUrl) {
    lateinit var chapter: ReaderChapter
}