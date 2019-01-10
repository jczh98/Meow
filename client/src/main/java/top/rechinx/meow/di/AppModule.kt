package top.rechinx.meow.di

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import okhttp3.Cache
import org.koin.android.ext.koin.androidApplication
import org.koin.dsl.module.module
import top.rechinx.meow.core.network.Http
import top.rechinx.meow.core.source.Dependencies
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.cache.ChapterCache
import top.rechinx.meow.data.cache.CoverCache
import top.rechinx.meow.rikka.rx.RxSchedulers
import java.io.File

val appModule = module(createOnStart = true) {

    single {
        val cacheDir = File(androidApplication().cacheDir, "network_cache")
        val cacheSize = 15L * 1024 * 1024
        val cache = Cache(cacheDir, cacheSize)
        Http(cache)
    }
    single { Dependencies(get()) }
    single { SourceManager(get()) }
    single { CoverCache(androidApplication()) }
    single { ChapterCache(androidApplication()) }
    single { RxSchedulers(Schedulers.io(), Schedulers.computation(), AndroidSchedulers.mainThread()) }
}