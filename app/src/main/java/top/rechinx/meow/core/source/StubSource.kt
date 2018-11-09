package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.*
import java.lang.Exception

class StubSource(override val id: Long): Source {

    override val name: String = id.toString()

    override fun fetchPopularManga(page: Int): Observable<PagedManga> {
        return Observable.error(Exception())
    }

    override fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<PagedManga> {
        return Observable.error(Exception())
    }

    override fun fetchMangaInfo(cid: String): Observable<AbsManga> {
        return Observable.error(Exception())
    }

    override fun fetchChapters(page: Int, cid: String): Observable<PagedList<AbsChapter>> {
        return Observable.error(Exception())
    }

    override fun fetchMangaPages(chapter: AbsChapter): Observable<List<AbsMangaPage>> {
        return Observable.error(Exception())
    }

    override fun getFilterList(): FilterList {
        return FilterList()
    }

}