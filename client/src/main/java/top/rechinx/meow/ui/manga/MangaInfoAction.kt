package top.rechinx.meow.ui.manga

import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.domain.chapter.model.ChaptersPage
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.ui.catalogbrowse.CatalogBrowseAction
import top.rechinx.meow.ui.catalogbrowse.CatalogBrowseViewState

sealed class MangaInfoAction {

    object LoadChapters : MangaInfoAction()

    object InitializeManga : MangaInfoAction()

    data class MangaInitialized(val source: CatalogSource, val manga: Manga) : MangaInfoAction() {
        override fun reduce(state: MangaInfoViewState) =
                state.copy(
                        source = source,
                        manga = manga
                )
    }

    data class PageReceived(val page: ChaptersPage) : MangaInfoAction() {
        override fun reduce(state: MangaInfoViewState) =
                state.copy(
                        chapters = state.chapters + page.chapters,
                        isLoading = false,
                        currentPage = page.number,
                        hasNextPage = page.hasNextPage
                )
    }

    data class Loading(val isLoading: Boolean) : MangaInfoAction() {
        override fun reduce(state: MangaInfoViewState) =
                state.copy(isLoading = isLoading)
    }

    data class LoadingError(val error: Throwable) : MangaInfoAction() {
        override fun reduce(state: MangaInfoViewState) =
                state.copy(error = error, isLoading = false)
    }

    open fun reduce(state: MangaInfoViewState) = state
}