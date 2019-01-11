package top.rechinx.meow.di.providers

import android.app.Application
import okhttp3.Cache
import toothpick.ProvidesSingletonInScope
import top.rechinx.meow.core.network.Http
import java.io.File
import javax.inject.Inject
import javax.inject.Provider
import javax.inject.Singleton

@Singleton
@ProvidesSingletonInScope
internal class HttpProvider @Inject constructor(
        private val context: Application
) : Provider<Http> {

    override fun get(): Http {
        val cacheDir = File(context.cacheDir, "network_cache")
        val cacheSize = 15L * 1024 * 1024
        val cache = Cache(cacheDir, cacheSize)
        return Http(cache)
    }

}