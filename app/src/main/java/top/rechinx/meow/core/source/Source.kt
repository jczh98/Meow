package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.AbsChapter
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.AbsMangaPage

abstract class Source {

    abstract val id: Long

    abstract val name: String

    abstract fun fetchLatestManga(page: Int): Observable<List<AbsManga>>

    abstract fun fetchSearchManga(keyword: String, page: Int, filters: FilterList): Observable<List<AbsManga>>

    abstract fun fetchMangaInfo(cid: String): Observable<AbsManga>

    abstract fun fetchChapters(cid: String): Observable<List<AbsChapter>>

    abstract fun fetchMangaPages(chapter: AbsChapter): Observable<List<AbsMangaPage>>

    abstract fun getFilterList(): FilterList
}