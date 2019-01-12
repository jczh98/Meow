package top.rechinx.meow.ui.catalogbrowse

import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.domain.manga.model.Manga

data class CatalogBrowseViewState(
        val source: CatalogSource? = null,
        val mangas: List<Manga> = emptyList(),
        val isLoading: Boolean = false,
        val currentPage: Int = 0,
        val hasMorePages: Boolean = true,
        val error: Throwable? = null
)