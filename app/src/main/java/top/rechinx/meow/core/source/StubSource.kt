package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.AbsChapter
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.AbsMangaPage
import top.rechinx.meow.core.source.model.FilterList
import java.lang.Exception

class StubSource(override val id: Long): Source() {

    override val name: String = id.toString()

    override fun fetchPopularManga(page: Int): Observable<List<AbsManga>> {
        return Observable.error(Exception())
    }

    override fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<List<AbsManga>> {
        return Observable.error(Exception())
    }

    override fun fetchMangaInfo(cid: String): Observable<AbsManga> {
        return Observable.error(Exception())
    }

    override fun fetchChapters(cid: String): Observable<List<AbsChapter>> {
        return Observable.error(Exception())
    }

    override fun fetchMangaPages(chapter: AbsChapter): Observable<List<AbsMangaPage>> {
        return Observable.error(Exception())
    }

    override fun getFilterList(): FilterList {
        return FilterList()
    }

}