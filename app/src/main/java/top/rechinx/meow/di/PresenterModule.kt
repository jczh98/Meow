package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.ui.grid.favorite.FavoriteContract
import top.rechinx.meow.ui.grid.favorite.FavoritePresenter
import top.rechinx.meow.ui.grid.history.HistoryContract
import top.rechinx.meow.ui.grid.history.HistoryPresenter
import top.rechinx.meow.ui.reader.ReaderContarct
import top.rechinx.meow.ui.reader.ReaderPresenter
import top.rechinx.meow.ui.result.ResultContract
import top.rechinx.meow.ui.result.ResultPresenter

object PresenterModule {

    val presenterModule = module {

        factory<ResultContract.Presenter> { ResultPresenter(get()) }

        factory<ReaderContarct.Presenter> { ReaderPresenter(get(), get(), get()) }

        factory<HistoryContract.Presenter> { HistoryPresenter(get()) }

        factory<FavoriteContract.Presenter> { FavoritePresenter(get()) }
    }
}