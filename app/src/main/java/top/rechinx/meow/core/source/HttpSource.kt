package top.rechinx.meow.core.source

import io.reactivex.Observable
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import top.rechinx.meow.core.network.NetworkHelper
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.network.asObservableSuccess
import top.rechinx.meow.core.network.newCallWithProgress
import top.rechinx.meow.core.source.model.*
import java.security.MessageDigest

abstract class HttpSource: Source, KoinComponent {

    override val id by lazy {
        val key = "${name.toLowerCase()}"
        val bytes = MessageDigest.getInstance("MD5").digest(key.toByteArray())
        (0..7).map { bytes[it].toLong() and 0xff shl 8 * (7 - it) }.reduce(Long::or) and Long.MAX_VALUE
    }

    protected val networkHelper: NetworkHelper by inject()

    val headers: Headers by lazy { headersBuilder().build() }

    open val client: OkHttpClient
        get() = networkHelper.client

    open protected fun headersBuilder() = Headers.Builder().apply {
        add("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)")
    }

    override fun fetchPopularManga(page: Int): Observable<PagedManga> = client.newCall(popularMangaRequest(page))
            .asObservableSuccess()
            .map { response ->
                popularMangaParse(response)
            }


    override fun fetchSearchManga(query: String, page: Int, filters: FilterList): Observable<PagedManga> = client.newCall(searchMangaRequest(query, page, filters))
            .asObservableSuccess()
            .map { response ->
                searchMangaParse(response)
            }

    override fun fetchMangaInfo(cid: String): Observable<AbsManga> = client.newCall(mangaInfoRequest(cid))
            .asObservableSuccess()
            .map { response ->
                mangaInfoParse(response).apply { initialized = true }
            }

    override fun fetchChapters(page: Int, cid: String): Observable<PagedList<AbsChapter>> = client.newCall(chaptersRequest(page, cid))
            .asObservableSuccess()
            .map { response ->
                chaptersParse(response)
            }

    override fun fetchMangaPages(chapter: AbsChapter): Observable<List<AbsMangaPage>> = client.newCall(mangaPagesRequest(chapter))
            .asObservableSuccess()
            .map { response ->
                mangaPagesParse(response)
            }

    fun fetchImage(page: AbsMangaPage): Observable<Response> {
        return client.newCallWithProgress(imageRequest(page), page)
                .asObservableSuccess()
    }

    open protected fun imageRequest(page: AbsMangaPage): Request {
        return Request.Builder()
                .url(page.imageUrl!!)
                .headers(headers)
                .build()
    }

    protected abstract val baseUrl: String

    protected abstract fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request

    protected abstract fun searchMangaParse(response: Response): PagedManga

    protected abstract fun popularMangaRequest(page: Int): Request

    protected abstract fun popularMangaParse(response: Response): PagedManga

    protected abstract fun mangaInfoRequest(cid: String): Request

    protected abstract fun mangaInfoParse(response: Response): AbsManga

    protected abstract fun chaptersRequest(page: Int, cid: String): Request

    protected abstract fun chaptersParse(response: Response): PagedList<AbsChapter>

    protected abstract fun mangaPagesRequest(chapter: AbsChapter): Request

    protected abstract fun mangaPagesParse(response: Response): List<AbsMangaPage>
}