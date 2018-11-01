package top.rechinx.meow.di

import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module.module
import top.rechinx.meow.App
import top.rechinx.meow.core.network.NetworkHelper
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.cache.ChapterCache
import top.rechinx.meow.support.preference.PreferenceHelper
import top.rechinx.meow.ui.result.ResultContract
import top.rechinx.meow.ui.result.ResultPresenter

object AppModule {

    val appModule = module(createOnStart = true) {
        single { NetworkHelper(androidApplication()) }
        single { SourceManager(androidApplication()) }
        single { PreferenceHelper(androidApplication())}
        single { ChapterCache(androidApplication()) }
    }

}