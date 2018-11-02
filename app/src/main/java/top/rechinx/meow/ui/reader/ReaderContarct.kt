package top.rechinx.meow.ui.reader

import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.mvp.BasePresenter
import top.rechinx.meow.support.mvp.BaseView
import top.rechinx.meow.ui.reader.model.ReaderChapter
import top.rechinx.meow.ui.reader.model.ReaderPage
import top.rechinx.meow.ui.reader.model.ViewerChapters

interface ReaderContarct {

    interface View: BaseView<Presenter> {

        fun setManga(manga: Manga)

        fun setChapters(viewerChapters: ViewerChapters)

    }

    interface Presenter: BasePresenter<View> {

        fun needsInit() : Boolean

        fun loadInit(mangaId: Long, chapterId: Long, isContinued: Boolean = false)

        fun preloadChapter(chapter: ReaderChapter)

        fun onPageSelected(page: ReaderPage)

        fun getMangaViewer() : Int

        fun setMangaViewer(viewer: Int)

        fun getCurrentChapter(): ReaderChapter?

        fun saveLastRead(chapter: ReaderChapter?)
    }
}