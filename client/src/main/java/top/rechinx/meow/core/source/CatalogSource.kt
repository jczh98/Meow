package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PagedList

interface CatalogSource : Source {

    /**
     * Returns an observable with all popular mangas in [page]
     *
     * @param page request page
     */
    fun fetchPopularMangas(page: Int): Observable<PagedList<MangaInfo>>

    /**
     * Returns an observable with all mangas which satisfies the query conditions
     *
     * @param query keyword for query a manga
     * @param page request page
     * @param filters filters for query a manga
     */
    fun fetchSearchMangas(query: String, page: Int, filters: FilterList) : Observable<PagedList<MangaInfo>>

    /**
     * Returns all filters for current source
     */
    fun getFilterList(): FilterList
}