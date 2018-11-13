package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.*

interface Source {

    val id: Long

    val name: String

    fun fetchPopularManga(page: Int): Observable<PagedList<SManga>>

    fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<PagedList<SManga>>

    fun fetchMangaInfo(url: String): Observable<SManga>

    fun fetchChapters(page: Int, url: String): Observable<PagedList<SChapter>>

    fun fetchMangaPages(chapter: SChapter): Observable<List<MangaPage>>

    fun getFilterList(): FilterList
}