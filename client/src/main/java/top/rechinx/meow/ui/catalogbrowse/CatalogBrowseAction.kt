package top.rechinx.meow.ui.catalogbrowse

import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.model.MangasPage
import top.rechinx.meow.ui.catalogbrowse.filters.FilterWrapper
import top.rechinx.meow.ui.catalogbrowse.filters.QueryMode

sealed class CatalogBrowseAction {

    object LoadMore : CatalogBrowseAction()

    sealed class SetQueryMode : CatalogBrowseAction() {
        object Listing : SetQueryMode()
        data class Search(val query: String) : SetQueryMode()
        data class Filters(val filters: List<FilterWrapper<*>>) : SetQueryMode()
    }

    data class QueryModeUpdated(val mode: QueryMode) : CatalogBrowseAction() {
        override fun reduce(state: CatalogBrowseViewState) =
                state.copy(
                        queryMode = mode,
                        mangas = emptyList(),
                        currentPage = 0,
                        hasMorePages = true,
                        isLoading = false
                )
    }

    data class PageReceived(val page: MangasPage) : CatalogBrowseAction() {
        override fun reduce(state: CatalogBrowseViewState) =
                state.copy(
                        mangas = state.mangas + page.mangas,
                        isLoading = false,
                        currentPage = page.number,
                        hasMorePages = page.hasNextPage
                )
    }

    data class Loading(val isLoading: Boolean, val page: Int) : CatalogBrowseAction() {
        override fun reduce(state: CatalogBrowseViewState) =
                state.copy(isLoading = isLoading)
    }

    data class LoadingError(val error: Throwable?) : CatalogBrowseAction() {
        override fun reduce(state: CatalogBrowseViewState) =
                state.copy(error = error, isLoading = false)
    }

    object ErrorDelivered : CatalogBrowseAction() {
        override fun reduce(state: CatalogBrowseViewState) =
                state.copy(error = null)
    }

    open fun reduce(state: CatalogBrowseViewState) = state

}