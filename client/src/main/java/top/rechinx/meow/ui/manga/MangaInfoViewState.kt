package top.rechinx.meow.ui.manga

import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga

data class MangaInfoViewState(
        val source: CatalogSource? = null,
        val manga: Manga? = null,
        val chapters: List<Chapter> = emptyList(),
        val isLoading: Boolean = false,
        val currentPage: Int = 0,
        val hasNextPage: Boolean = true,
        val error: Throwable? = null
)