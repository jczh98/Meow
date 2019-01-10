package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.ChapterInfo
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PageInfo
import top.rechinx.meow.core.source.model.PagedList
import java.lang.Exception

class StubSource(override val id: Long) : Source {

    override val name: String
        get() = id.toString()

    override fun fetchMangaInfo(manga: MangaInfo): MangaInfo {
        throw Exception()
    }

    override fun fetchChapterList(manga: MangaInfo, page: Int): PagedList<ChapterInfo> {
        throw Exception()
    }

    override fun fetchPageList(chapter: ChapterInfo): List<PageInfo> {
        throw Exception()
    }

}