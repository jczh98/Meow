package top.rechinx.meow.ui.details

import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.mvp.BasePresenter
import top.rechinx.meow.support.mvp.BaseView

interface DetailContract {

    interface View: BaseView<Presenter> {

        fun onMangaLoadCompleted(manga: Manga, needsChaptersRefresh: Boolean = true)

        fun onMangaFetchError()

        fun onChaptersInit(chapters: List<Chapter>)

        fun onChaptersFetchError()
    }

    interface Presenter: BasePresenter<View> {

        fun fetchMangaInfo(sourceId: Long, cid: String, needsChaptersRefresh: Boolean = true)

        fun fetchMangaChapters(sourceId: Long, cid: String)

        fun favoriteOrNot(manga: Manga)

        fun markedAsHistory(manga: Manga)
    }

}