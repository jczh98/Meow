package top.rechinx.meow.ui.catalogbrowse

import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.ui.catalogbrowse.filters.QueryMode

data class CatalogBrowseViewState(
        val source: CatalogSource? = null,
        val mangas: List<Manga> = emptyList(),
        val queryMode: QueryMode? = null,
        val filters: FilterList = FilterList(),
        val isLoading: Boolean = false,
        val currentPage: Int = 0,
        val hasMorePages: Boolean = true,
        val error: Throwable? = null
)