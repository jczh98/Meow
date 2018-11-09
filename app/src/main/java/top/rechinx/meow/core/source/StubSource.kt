package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.*
import java.lang.Exception

class StubSource(override val id: Long): Source {

    override val name: String = id.toString()

    override fun fetchPopularManga(page: Int): Observable<PagedList<SManga>> {
        return Observable.error(Exception())
    }

    override fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<PagedList<SManga>> {
        return Observable.error(Exception())
    }

    override fun fetchMangaInfo(cid: String): Observable<SManga> {
        return Observable.error(Exception())
    }

    override fun fetchChapters(page: Int, cid: String): Observable<PagedList<SChapter>> {
        return Observable.error(Exception())
    }

    override fun fetchMangaPages(chapter: SChapter): Observable<List<MangaPage>> {
        return Observable.error(Exception())
    }

    override fun getFilterList(): FilterList {
        return FilterList()
    }

}