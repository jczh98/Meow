package top.rechinx.meow.ui.catalogbrowse.filters

import top.rechinx.meow.core.source.model.FilterList

/**
 * Query mode which is [Listing] or [Filter]
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
    data class Filter(val filters: FilterList) : QueryMode()

}