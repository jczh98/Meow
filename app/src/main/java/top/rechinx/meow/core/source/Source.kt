package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.*

interface Source {

    val id: Long

    val name: String

    fun fetchPopularManga(page: Int): Observable<PagedList<AbsManga>>

    fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<PagedList<AbsManga>>

    fun fetchMangaInfo(cid: String): Observable<AbsManga>

    fun fetchChapters(page: Int, cid: String): Observable<PagedList<AbsChapter>>

    fun fetchMangaPages(chapter: AbsChapter): Observable<List<AbsMangaPage>>

    fun getFilterList(): FilterList
}