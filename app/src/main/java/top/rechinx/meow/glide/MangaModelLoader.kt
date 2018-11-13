package top.rechinx.meow.glide

import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.*
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.network.NetworkHelper
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.log.L
import java.io.InputStream

class MangaModelLoader: ModelLoader<Manga, InputStream>, KoinComponent {

    private val sourceManager: SourceManager by inject()
    private val networkHelper: NetworkHelper by inject()

    private val cachedHeaders = hashMapOf<Long, LazyHeaders>()

    class Factory : ModelLoaderFactory<Manga, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Manga, InputStream> {
            return MangaModelLoader()
        }

        override fun teardown() {}
    }

    override fun buildLoadData(manga: Manga, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val url = manga.thumbnail_url
        if(url == null || url.isEmpty()) return null

        if (url.startsWith("http")) {
            val source = sourceManager.getOrStub(manga.sourceId) as HttpSource
            val glideUrl = GlideUrl(url, getHeaders(manga, source))
            // Get the resource fetcher for this request url.
            val networkFetcher = OkHttpStreamFetcher(source.client ?: networkHelper.client, glideUrl)

            // Return an instance of the fetcher providing the needed elements.
            return ModelLoader.LoadData(glideUrl, networkFetcher)
        } else {
            return null
        }
    }

    override fun handles(model: Manga): Boolean = true

    fun getHeaders(manga: Manga, source: HttpSource?): Headers {
        if (source == null) return LazyHeaders.DEFAULT

        return cachedHeaders.getOrPut(manga.sourceId) {
            LazyHeaders.Builder().apply {
                val nullStr: String? = null
                setHeader("User-Agent", nullStr)
                for ((key, value) in source.headers.toMultimap()) {
                    addHeader(key, value[0])
                }
            }.build()
        }
    }

}