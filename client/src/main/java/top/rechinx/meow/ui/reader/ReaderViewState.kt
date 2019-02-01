package top.rechinx.meow.ui.reader

import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.page.model.ViewerChapters
import top.rechinx.meow.ui.reader.viewer.BaseViewer

data class ReaderViewState(
        val source: CatalogSource? = null,
        val manga: Manga? = null,
        val viewer: BaseViewer? = null,
        val chapters: ViewerChapters? = null
) {
    override fun toString(): String {
        return "ReaderViewState(" +
                "source=${source?.name}, " +
                "manga=${manga?.title}, " +
                "chapters=${chapters?.currChapter?.chapter?.name}, " +
                "viewer=${manga?.viewer})"
    }
}