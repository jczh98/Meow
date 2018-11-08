package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.*

abstract class Source {

    abstract val id: Long

    abstract val name: String

    abstract fun fetchPopularManga(page: Int): Observable<PagedManga>

    abstract fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<PagedManga>

    abstract fun fetchMangaInfo(cid: String): Observable<AbsManga>

    abstract fun fetchChapters(cid: String): Observable<List<AbsChapter>>

    abstract fun fetchMangaPages(chapter: AbsChapter): Observable<List<AbsMangaPage>>

    abstract fun getFilterList(): FilterList
}