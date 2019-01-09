package top.rechinx.meow.glide

import android.util.LruCache
import com.bumptech.glide.integration.okhttp3.OkHttpStreamFetcher
import com.bumptech.glide.load.Options
import com.bumptech.glide.load.model.*
import org.koin.core.Koin
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.cache.CoverCache
import top.rechinx.meow.domain.manga.model.Manga
import java.io.File
import java.io.InputStream

class MangaModelLoader: ModelLoader<Manga, InputStream>, KoinComponent {

    private val sourceManager: SourceManager by inject()

    private val coverCache: CoverCache by inject()

    private val cachedHeaders = hashMapOf<Long, LazyHeaders>()

    private val lruCache = LruCache<GlideUrl, File>(100)

    class Factory : ModelLoaderFactory<Manga, InputStream> {

        override fun build(multiFactory: MultiModelLoaderFactory): ModelLoader<Manga, InputStream> {
            return MangaModelLoader()
        }

        override fun teardown() {}
    }

    override fun buildLoadData(manga: Manga, width: Int, height: Int, options: Options): ModelLoader.LoadData<InputStream>? {
        val url = manga.cover
        if(url.isEmpty()) return null

        if (url.startsWith("http")) {
            val source = sourceManager.getOrStub(manga.source) as HttpSource
            val glideUrl = GlideUrl(url, getHeaders(manga, source))
            // Get the resource fetcher for this request url.
            val networkFetcher = OkHttpStreamFetcher(source.client, glideUrl)

            // Get file from local cache
            val file = lruCache.getOrPut(glideUrl) { coverCache.getCoverFile(url) }

            val libraryFetcher = LibraryMangaUrlFetcher(networkFetcher, manga, file)

            // If have local cache, return local cache first
            return ModelLoader.LoadData(MangaSignature(manga, file), libraryFetcher)
        } else {
            // Get the file from the url, removing the scheme if present.
            val file = File(url.substringAfter("file://"))

            // Return an instance of the fetcher providing the needed elements.
            return ModelLoader.LoadData(MangaSignature(manga, file), FileFetcher(file))
        }
    }

    override fun handles(model: Manga): Boolean = true

    /**
     * Returns the request header from specific sourceId and caching it
     *
     * @param manga the model
     * @param source specific sourceId
     */
    fun getHeaders(manga: Manga, source: HttpSource?): Headers {
        if (source == null) return LazyHeaders.DEFAULT

        return cachedHeaders.getOrPut(manga.source) {
            LazyHeaders.Builder().apply {
                val nullStr: String? = null
                setHeader("User-Agent", nullStr)
                for ((key, value) in source.headers.toMultimap()) {
                    addHeader(key, value[0])
                }
            }.build()
        }
    }

    private inline fun <K, V> LruCache<K, V>.getOrPut(key: K, defaultValue: () -> V): V {
        val value = get(key)
        return if (value == null) {
            val answer = defaultValue()
            put(key, answer)
            answer
        } else {
            value
        }
    }

}