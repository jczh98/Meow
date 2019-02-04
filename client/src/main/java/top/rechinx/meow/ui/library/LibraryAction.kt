package top.rechinx.meow.ui.library

import top.rechinx.meow.core.source.Source
import top.rechinx.meow.domain.manga.model.Manga

sealed class LibraryAction {

    object LoadSubscribedMangaList : LibraryAction()

    data class SubscribedMangaLoaded(val mangaList: List<Pair<Manga, Source>>) : LibraryAction() {
        override fun reduce(state: LibraryViewState): LibraryViewState =
                state.copy(
                        mangaList = mangaList
                )
    }

    open fun reduce(state: LibraryViewState) = state
}
