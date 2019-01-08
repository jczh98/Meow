package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.ChapterInfo
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PageInfo
import top.rechinx.meow.core.source.model.PagedList

/**
 * Basic interface of source
 */
interface Source {

    /**
     * Unique id for the source
     */
    val id: Long

    /**
     * name for the source
     */
    val name: String

    /**
     * Returns an observable with a manga info
     *
     * @param manga the manga needed to query
     */
    fun fetchMangaInfo(manga: MangaInfo) : Observable<MangaInfo>

    /**
     * Returns an observable with chapters in [page] page
     *
     * @param manga the manga needed to query
     * @param page the current page
     */
    fun fetchChapterList(manga: MangaInfo, page: Int) : Observable<PagedList<ChapterInfo>>

    /**
     * Returns an observable with list of pages the [chapter] has
     *
     * @param chapter the chapter needed to query
     */
    fun fetchPageList(chapter: ChapterInfo) : Observable<List<PageInfo>>
}