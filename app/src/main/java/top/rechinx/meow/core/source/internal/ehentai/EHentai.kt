package top.rechinx.meow.core.source.internal.ehentai

import android.net.Uri
import io.reactivex.Observable
import okhttp3.CacheControl
import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import org.jsoup.nodes.Element
import top.rechinx.meow.core.network.asObservableSuccess
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*
import java.net.URI
import java.net.URISyntaxException
import java.net.URLEncoder
import java.util.*
import java.util.concurrent.TimeUnit

class EHentai : HttpSource() {

    override val baseUrl: String
        get() = "https://e-hentai.org"

    fun GET(url: String, page: Int? = null, additionalHeaders: Headers? = null, cache: Boolean = true) = Request.Builder()
            .url(page?.let {
                addParam(url, "page", (it - 1).toString())
            } ?: url)
            .headers(additionalHeaders?.let {
                val headers = headers.newBuilder()
                it.toMultimap().forEach { (t, u) ->
                    u.forEach {
                        headers.add(t, it)
                    }
                }
                headers.build()
            } ?: headers)
            .cacheControl(if (!cache) CacheControl.FORCE_NETWORK else CacheControl.Builder().maxAge(10, TimeUnit.MINUTES).build())
            .build()


    fun addParam(url: String, param: String, value: String) = Uri.parse(url)
            .buildUpon()
            .appendQueryParameter(param, value)
            .toString()

    override val client = networkHelper.client.newBuilder()
            .addNetworkInterceptor { chain ->
                val newReq = chain
                        .request()
                        .newBuilder()
                        .addHeader("Cookie", cookiesHeader)
                        .build()

                chain.proceed(newReq)
            }.build()!!

    val cookiesHeader by lazy {
        val cookies = mutableMapOf<String, String>()

        //Setup settings
        val settings = mutableListOf<String>()

        //Do not show popular right now pane as we can't parse it
        settings += "prn_n"

        //Exclude every other language except the one we have selected
        settings += "xl_" + languageMappings.filter { it.first != "english" }
                .flatMap { it.second }
                .joinToString("x")

        cookies.put("uconfig", buildSettings(settings))

        buildCookies(cookies)
    }

    fun buildSettings(settings: List<String?>) = settings.filterNotNull().joinToString(separator = "-")

    fun buildCookies(cookies: Map<String, String>) = cookies.entries.map {
        "${URLEncoder.encode(it.key, "UTF-8")}=${URLEncoder.encode(it.value, "UTF-8")}"
    }.joinToString(separator = "; ", postfix = ";")


    fun commonMangaParse(doc: Document) = with(doc) {
        //Parse mangas
        val parsedMangas = select(".gtr0,.gtr1").map {
            //fav = it.select(".itd .it3 > .i[id]").attr("title"),
            SManga.create().apply {
                //Get title
                it.select(".itd .it5 a").apply {
                    title = text()
                    url = setUrlWithoutDomain(addParam(attr("href"), "nw", "always"))
                }
                //Get image
                it.select(".itd .it2").first().apply {
                    children().first()?.let {
                        thumbnail_url = it.attr("src")
                    } ?: text().split("~").apply {
                        thumbnail_url = "http://${this[1]}/${this[2]}"
                    }
                }
            }
        }
        //Add to page if required
        val hasNextPage = select("a[onclick=return false]").last()?.text() == ">"
        PagedList(parsedMangas, hasNextPage)
    }

    private fun setUrlWithoutDomain(orig: String): String {
        try {
            val uri = URI(orig)
            var out = uri.path
            if (uri.query != null)
                out += "?" + uri.query
            if (uri.fragment != null)
                out += "#" + uri.fragment
            return out
        } catch (e: URISyntaxException) {
            return orig
        }
    }

    override fun searchMangaRequest(query: String, page: Int, filters: FilterList): Request {
        val uri = Uri.parse("$baseUrl$QUERY_PREFIX").buildUpon()
        uri.appendQueryParameter("f_search", query)
        filters.forEach {
            if (it is UriFilter) it.addToUri(uri)
        }
        return GET(uri.toString(), page)
    }

    override fun searchMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response.asJsoup())

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl, page)

    override fun popularMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response.asJsoup())

    override fun mangaInfoRequest(cid: String): Request = GET(baseUrl + cid, null, headers)


    override fun mangaInfoParse(response: Response) = with(response.asJsoup()) {
        with(ExGalleryMetadata()) {
            url = response.request().url().encodedPath()
            title = select("#gn").text().nullIfBlank()?.trim()

            altTitle = select("#gj").text().nullIfBlank()?.trim()

            //Thumbnail is set as background of element in style attribute
            thumbnailUrl = select("#gd1 div").attr("style").nullIfBlank()?.let {
                it.substring(it.indexOf('(') + 1 until it.lastIndexOf(')'))
            }

            genre = select(".ic").parents().attr("href").nullIfBlank()?.trim()?.substringAfterLast('/')

            uploader = select("#gdn").text().nullIfBlank()?.trim()

            //Parse the table
            select("#gdd tr").forEach {
                val left = it.select(".gdt1").text().nullIfBlank()?.trim() ?: return@forEach
                val right = it.select(".gdt2").text().nullIfBlank()?.trim() ?: return@forEach
                ignore {
                    when (left.removeSuffix(":")
                            .toLowerCase()) {
                        "posted" -> datePosted = EX_DATE_FORMAT.parse(right).time
                        "visible" -> visible = right.nullIfBlank()
                        "language" -> {
                            language = right.removeSuffix(TR_SUFFIX).trim().nullIfBlank()
                            translated = right.endsWith(TR_SUFFIX, true)
                        }
                        "file size" -> size = parseHumanReadableByteCount(right)?.toLong()
                        "length" -> length = right.removeSuffix("pages").trim().nullIfBlank()?.toInt()
                        "favorited" -> favorites = right.removeSuffix("times").trim().nullIfBlank()?.toInt()
                    }
                }
            }

            //Parse ratings
            ignore {
                averageRating = getElementById("rating_label")
                        .text()
                        .removePrefix("Average:")
                        .trim()
                        .nullIfBlank()
                        ?.toDouble()
                ratingCount = getElementById("rating_count")
                        .text()
                        .trim()
                        .nullIfBlank()
                        ?.toInt()
            }

            //Parse tags
            tags.clear()
            select("#taglist tr").forEach {
                val namespace = it.select(".tc").text().removeSuffix(":")
                val currentTags = it.select("div").map {
                    Tag(it.text().trim(),
                            it.hasClass("gtl"))
                }
                tags.put(namespace, currentTags)
            }

            //Copy metadata to manga
            SManga.create().apply {
                copyTo(this)
            }
        }
    }

    override fun fetchChapters(page: Int, cid: String): Observable<PagedList<SChapter>> = Observable.just(PagedList(listOf(SChapter.create().apply {
        url = cid
        name = "Chapter"
        chapter_number = "1"
    }), false))


    override fun fetchMangaPages(chapter: SChapter): Observable<List<MangaPage>> = fetchChapterPage(chapter, "$baseUrl/${chapter.url}").map {
        it.mapIndexed { i, s ->
            MangaPage(i, s, null)
        }
    }

    private fun fetchChapterPage(chapter: SChapter, np: String,
                                 pastUrls: List<String> = emptyList()): Observable<List<String>> {
        val urls = pastUrls.toMutableList()
        return chapterPageCall(np).flatMap {
            val jsoup = it.asJsoup()
            urls += parseChapterPage(jsoup)
            nextPageUrl(jsoup)?.let {
                fetchChapterPage(chapter, it, urls)
            } ?: Observable.just(urls)
        }
    }

    private fun parseChapterPage(response: Element) = with(response) {
        select(".gdtm a").map {
            Pair(it.child(0).attr("alt").toInt(), it.attr("href"))
        }.sortedBy(Pair<Int, String>::first).map { it.second }
    }

    private fun nextPageUrl(element: Element) = element.select("a[onclick=return false]").last()?.let {
        if (it.text() == ">") it.attr("href") else null
    }

    private fun chapterPageCall(np: String) = client.newCall(chapterPageRequest(np)).asObservableSuccess()
    private fun chapterPageRequest(np: String) = GET(np, null, headers)

    override fun chaptersRequest(page: Int, cid: String): Request = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun chaptersParse(response: Response): PagedList<SChapter> = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun mangaPagesRequest(chapter: SChapter): Request = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun mangaPagesParse(response: Response): List<MangaPage> = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun imageUrlParse(response: Response): String = with(response.asJsoup()) {
        val currentImage = getElementById("img").attr("src")
        currentImage
    }

    val languageMappings = listOf(
            Pair("japanese", listOf("0", "1024", "2048")),
            Pair("english", listOf("1", "1025", "2049")),
            Pair("chinese", listOf("10", "1034", "2058")),
            Pair("dutch", listOf("20", "1044", "2068")),
            Pair("french", listOf("30", "1054", "2078")),
            Pair("german", listOf("40", "1064", "2088")),
            Pair("hungarian", listOf("50", "1074", "2098")),
            Pair("italian", listOf("60", "1084", "2108")),
            Pair("korean", listOf("70", "1094", "2118")),
            Pair("polish", listOf("80", "1104", "2128")),
            Pair("portuguese", listOf("90", "1114", "2138")),
            Pair("russian", listOf("100", "1124", "2148")),
            Pair("spanish", listOf("110", "1134", "2158")),
            Pair("thai", listOf("120", "1144", "2168")),
            Pair("vietnamese", listOf("130", "1154", "2178")),
            Pair("n/a", listOf("254", "1278", "2302")),
            Pair("other", listOf("255", "1279", "2303"))
    )


    fun Response.asJsoup(html: String? = null): Document {
        return Jsoup.parse(html ?: body()!!.string(), request().url().toString())
    }


    //Filters
    override fun getFilterList() = FilterList(
            GenreGroup(),
            AdvancedGroup()
    )

    class GenreOption(name: String, val genreId: String) : Filter.CheckBox(name, false), UriFilter {
        override fun addToUri(builder: Uri.Builder) {
            builder.appendQueryParameter("f_" + genreId, if (state) "1" else "0")
        }
    }

    class GenreGroup : UriGroup<GenreOption>("Genres", listOf(
            GenreOption("Dōjinshi", "doujinshi"),
            GenreOption("Manga", "manga"),
            GenreOption("Artist CG", "artistcg"),
            GenreOption("Game CG", "gamecg"),
            GenreOption("Western", "western"),
            GenreOption("Non-H", "non-h"),
            GenreOption("Image Set", "imageset"),
            GenreOption("Cosplay", "cosplay"),
            GenreOption("Asian Porn", "asianporn"),
            GenreOption("Misc", "misc")
    ))

    class AdvancedOption(name: String, val param: String, defValue: Boolean = false) : Filter.CheckBox(name, defValue), UriFilter {
        override fun addToUri(builder: Uri.Builder) {
            if (state)
                builder.appendQueryParameter(param, "on")
        }
    }

    class RatingOption : Filter.Select<String>("Minimum Rating", arrayOf(
            "Any",
            "2 stars",
            "3 stars",
            "4 stars",
            "5 stars"
    )), UriFilter {
        override fun addToUri(builder: Uri.Builder) {
            if (state > 0) builder.appendQueryParameter("f_srdd", Integer.toString(state + 1))
        }
    }

    //Explicit type arg for listOf() to workaround this: KT-16570
    class AdvancedGroup : UriGroup<Filter<*>>("Advanced Options", listOf<Filter<*>>(
            AdvancedOption("Search Gallery Name", "f_sname", true),
            AdvancedOption("Search Gallery Tags", "f_stags", true),
            AdvancedOption("Search Gallery Description", "f_sdesc"),
            AdvancedOption("Search Torrent Filenames", "f_storr"),
            AdvancedOption("Only Show Galleries With Torrents", "f_sto"),
            AdvancedOption("Search Low-Power Tags", "f_sdt1"),
            AdvancedOption("Search Downvoted Tags", "f_sdt2"),
            AdvancedOption("Show Expunged Galleries", "f_sh"),
            RatingOption()
    ))

    interface UriFilter {
        fun addToUri(builder: Uri.Builder)
    }

    open class UriGroup<V>(name: String, state: List<V>) : Filter.Group<V>(name, state), UriFilter {
        override fun addToUri(builder: Uri.Builder) {
            state.forEach {
                if (it is UriFilter) it.addToUri(builder)
            }
        }
    }

    override val name: String
        get() = "E-Hentai"

    companion object {
        const val QUERY_PREFIX = "?f_apply=Apply+Filter"
        const val TR_SUFFIX = "TR"
    }
}