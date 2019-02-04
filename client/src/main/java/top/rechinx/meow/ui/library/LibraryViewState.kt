package top.rechinx.meow.ui.library

import top.rechinx.meow.core.source.Source
import top.rechinx.meow.domain.manga.model.Manga

data class LibraryViewState(
        var mangaList: List<Pair<Manga, Source>> = emptyList()
)