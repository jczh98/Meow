package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PagedList

interface CatalogSource : Source {

    /**
     * Returns a list with all popular mangas in [page]
     *
     */
    fun fetchPopularMangas(page: Int): PagedList<MangaInfo>

    /**
     * Returns a list with all mangas which satisfies the query conditions [filters] and [query] in [page]
     *
     */
    fun fetchSearchMangas(query: String, page: Int, filters: FilterList) : PagedList<MangaInfo>

    /**
     * Returns all filters for current source
     */
    fun getFilterList(): FilterList
}