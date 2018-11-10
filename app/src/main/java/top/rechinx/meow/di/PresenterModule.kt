package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.ui.grid.history.HistoryPresenter
import top.rechinx.meow.ui.result.ResultContract
import top.rechinx.meow.ui.result.ResultPresenter

object PresenterModule {

    val presenterModule = module {

        factory<ResultContract.Presenter> { ResultPresenter(get()) }
    }
}