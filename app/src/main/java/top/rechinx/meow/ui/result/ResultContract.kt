package top.rechinx.meow.ui.result

import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.mvp.BasePresenter
import top.rechinx.meow.support.mvp.BaseView

class ResultContract {

    interface View : BaseView<Presenter> {

        fun onMangaLoadCompleted(manga: Manga)

        fun onLoadError()

        fun onLoadMoreCompleted()
    }

    interface Presenter: BasePresenter<View> {

        fun refresh(keyword: String)

        fun search(keyword: String, isLoadMore: Boolean = false)
    }
}