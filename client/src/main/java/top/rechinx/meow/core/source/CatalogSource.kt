package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PagedList

interface CatalogSource : Source {

    /**
     * Returns an observable with all popular mangas in [page]
     *
     */
    fun fetchPopularMangas(page: Int): Observable<PagedList<MangaInfo>>

    /**
     * Returns an observable with all mangas which satisfies the query conditions [filters] and [query] in [page]
     *
     */
    fun fetchSearchMangas(query: String, page: Int, filters: FilterList) : Observable<PagedList<MangaInfo>>

    /**
     * Returns all filters for current source
     */
    fun getFilterList(): FilterList
}