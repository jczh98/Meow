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
     * Returns a model of [MangaInfo]
     *
     */
    fun fetchMangaInfo(manga: MangaInfo) : MangaInfo

    /**
     * Returns a list of [ChapterInfo] with [page]
     *
     */
    fun fetchChapterList(manga: MangaInfo, page: Int) : PagedList<ChapterInfo>

    /**
     * Returns a list of [PageInfo]
     *
     */
    fun fetchPageList(chapter: ChapterInfo) : List<PageInfo>
}