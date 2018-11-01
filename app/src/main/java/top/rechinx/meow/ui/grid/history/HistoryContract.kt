package top.rechinx.meow.ui.grid.history

import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.mvp.BasePresenter
import top.rechinx.meow.support.mvp.BaseView

interface HistoryContract {

    interface View: BaseView<Presenter> {

        fun onMangasLoaded(list: List<Manga>)

        fun onMangasLoadError()
    }

    interface Presenter: BasePresenter<View> {

        fun load()

    }

}