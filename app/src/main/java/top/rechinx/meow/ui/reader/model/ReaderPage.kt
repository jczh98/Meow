package top.rechinx.meow.ui.reader.model

import top.rechinx.meow.core.source.model.MangaPage
import java.io.InputStream

class ReaderPage(index: Int,
                 url: String,
                 imageUrl: String?,
                 var stream: (() -> InputStream)? = null): MangaPage(index, url, imageUrl) {
    lateinit var chapter: ReaderChapter
}