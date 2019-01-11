package top.rechinx.meow.di

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import toothpick.ProvidesSingletonInScope
import toothpick.config.Module
import top.rechinx.meow.core.network.Http
import top.rechinx.meow.di.providers.HttpProvider
import top.rechinx.meow.core.source.Dependencies
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.cache.ChapterCache
import top.rechinx.meow.data.cache.CoverCache
import top.rechinx.meow.di.providers.RxSchedulersProvider
import top.rechinx.meow.rikka.rx.RxSchedulers
import javax.inject.Provider
import javax.inject.Singleton

object ApplicationModule : Module() {

    init {
        bindProvider<Http, HttpProvider>()
        bindProvider<RxSchedulers, RxSchedulersProvider>()
        bind(Dependencies::class.java).singletonInScope()
        bind(SourceManager::class.java).singletonInScope()
        bind(CoverCache::class.java).singletonInScope()
        bind(ChapterCache::class.java).singletonInScope()
    }
}