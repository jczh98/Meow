package top.rechinx.meow.di.modules

import android.app.Application
import dagger.Module
import dagger.Provides
import io.reactivex.android.schedulers.AndroidSchedulers
import okhttp3.Cache
import top.rechinx.meow.core.network.Http
import top.rechinx.meow.rikka.rx.RxSchedulers
import java.io.File
import javax.inject.Singleton
import io.reactivex.android.schedulers.AndroidSchedulers.mainThread
import io.reactivex.schedulers.Schedulers
import io.reactivex.schedulers.Schedulers.computation
import io.reactivex.schedulers.Schedulers.io
import top.rechinx.meow.core.source.Dependencies
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.cache.ChapterCache
import top.rechinx.meow.di.GlideComponent


@Module(
        subcomponents = [GlideComponent::class]
)
object AppModule {

    @JvmStatic @Provides @Singleton
    fun provideHttp(
            context: Application
    ) : Http {
        val cacheDir = File(context.cacheDir, "network_cache")
        val cacheSize = 15L * 1024 * 1024
        val cache = Cache(cacheDir, cacheSize)
        return Http(cache)
    }

    @JvmStatic @Provides @Singleton
    fun provideDependencies(
            http: Http
    ) : Dependencies = Dependencies(http)

    @JvmStatic @Provides @Singleton
    fun provideSourceManager(
            dependencies: Dependencies
    ) : SourceManager = SourceManager(dependencies)

    @JvmStatic @Provides @Singleton
    fun provideRxSchedulers() : RxSchedulers
        = RxSchedulers(
            io = Schedulers.io(),
            computation = Schedulers.computation(),
            main = AndroidSchedulers.mainThread())

    @JvmStatic @Provides @Singleton
    fun provideChapterCache(
            context: Application
    ) : ChapterCache = ChapterCache(context)

}