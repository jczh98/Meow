package top.rechinx.meow.core

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

    override fun fetchMangaInfo(manga: MangaInfo): Observable<MangaInfo> {
        return Observable.error(Exception())
    }

    override fun fetchChapterList(manga: MangaInfo, page: Int): Observable<PagedList<ChapterInfo>> {
        return Observable.error(Exception())
    }

    override fun fetchPageList(chapter: ChapterInfo): Observable<List<PageInfo>> {
        return Observable.error(Exception())
    }

}