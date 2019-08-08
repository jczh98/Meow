package top.rechinx.meow.core.source.internal

import android.util.Base64
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.jsoup.Jsoup
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*

class Tencent:HttpSource() {
    override val name = "Tencent"
    override val baseUrl = "https://ac.qq.com"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request = GET("$baseUrl/Comic/searchList?search=$keyword&page=$page")

    override fun searchMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".mod_book_list li").map { node -> SManga.create().apply {
            title = node.selectFirst("h4").text()
            thumbnail_url  = node.selectFirst("img").attr("data-original")
            url = baseUrl + node.selectFirst("a").attr("href")
        } }
        return PagedList(ret, true/*doc.selectFirst(".mod_page_next") != null*/)
    }

    override fun popularMangaRequest(page: Int): Request = GET("$baseUrl/Comic/all/search/time/page/$page")

    override fun popularMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".ret-search-list li").map { node -> SManga.create().apply {
            title = node.selectFirst("h3").text()
            thumbnail_url  = node.selectFirst("img").attr("data-original")
            url = baseUrl+ node.selectFirst("a").attr("href")
            author = node.selectFirst(".ret-works-author").text()
        } }
        return PagedList(ret, true/*doc.selectFirst(".mod_page_next") != null*/)
    }

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga = SManga.create().apply {
        Jsoup.parse(response.body()!!.string()).selectFirst(".works-intro-wr").let {
            title = it.selectFirst("h2").text()
            thumbnail_url = it.selectFirst(".works-cover img").attr("src")
            author = it.selectFirst(".works-intro-digi em").text()
//            status = when(it.selectFirst(".comic_infor_status span").text()) {
////                "已完结" -> SManga.COMPLETED
////                "连载中" -> SManga.ONGOING
////                else -> SManga.UNKNOWN
////            }
            genre = it.select(".works-intro-tags a")
                    .map{ node -> node.text() }.joinToString(", ")
            description = it.selectFirst(".works-intro-short").text()
        }
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".chapter-page-all span").map { node -> SChapter.create().apply {
            name = when(node.selectFirst("i").className()){
                "ui-icon-pay" -> "🔒" + node.text()
                else -> node.text()
            }
            url = node.selectFirst("a").attr("href")
        } }
        return PagedList(ret.reversed(), false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request = GET(baseUrl + chapter.url!!)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val res = response.body()!!.string()
        val DATA = res.substringAfter("DATA        = '").substringBefore("'")
        val pic = String(Base64.decode(DATA.substring(DATA.length%4), Base64.NO_WRAP))
        val arr = JSONArray(pic.substringAfter("\"picture\":").substringBefore("]")+"]")
        val ret = ArrayList<MangaPage>()
        for (i in 0 until arr.length()) {
            ret.add(MangaPage(i, "", arr.getJSONObject(i).getString("url")))
        }
        return ret
    }

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun getFilterList(): FilterList = FilterList()
}