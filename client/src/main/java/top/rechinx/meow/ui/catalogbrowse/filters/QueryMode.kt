package top.rechinx.meow.ui.catalogbrowse.filters

import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.core.source.model.FilterList

/**
 * Query mode which is [Listing] or [FilterMode]
 */
sealed class QueryMode {

    /**
     * Query to use when requesting a list
     */
    object Listing : QueryMode()

    data class Search(val query: String)  : QueryMode()

    /**
     * Querty to use filtering the catalog with genres, categories and so on
     */
    data class FilterMode(val filters: List<Filter<*>>) : QueryMode()

}