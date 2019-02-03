package top.rechinx.meow.ui.reader

import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.page.model.ReaderChapter
import top.rechinx.meow.domain.page.model.ViewerChapters

sealed class ReaderAction {

    object InitializeManga : ReaderAction()

    data class LoadChapter(val chapter: ReaderChapter, val continued: Boolean = false) : ReaderAction()

    data class PreloadChapter(val chapter: ReaderChapter) : ReaderAction()

    data class SetMangaViewer(val viewer: Int) : ReaderAction()

    object ChapterPreloaded : ReaderAction()

    data class SetChapters(val viewerChapters: ViewerChapters) : ReaderAction() {
        override fun reduce(state: ReaderViewState): ReaderViewState =
                state.copy(
                        chapters = viewerChapters
                )
    }

    data class MangaViewerChanged(val manga: Manga, val chapters: ViewerChapters) : ReaderAction() {
        override fun reduce(state: ReaderViewState): ReaderViewState =
                state.copy(
                        manga = manga,
                        chapters = chapters,
                        viewer = manga.viewer
                )
    }

    data class MangaInitialized(val source: CatalogSource, val manga: Manga) : ReaderAction() {
        override fun reduce(state: ReaderViewState) =
                state.copy(
                        source = source,
                        manga = manga,
                        viewer = manga.viewer
                )
    }

    open fun reduce(state: ReaderViewState) = state
}