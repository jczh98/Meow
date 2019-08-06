package top.rechinx.meow.core.source.internal

import android.util.Base64
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.jsoup.Jsoup
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*

class U17:HttpSource() {

    override val name = "U17"
    override val baseUrl = "http://www.u17.com"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request = GET("http://so.u17.com/all/$keyword/m0_p$page.html")

    override fun searchMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".comiclist li").map { node -> SManga.create().apply {
            title = node.selectFirst("h3 a").text()
            thumbnail_url  = node.selectFirst("img").attr("src")
            url = node.selectFirst("a").attr("href")
            author = node.select("h3>a")?.text()
        } }
        return PagedList(ret, doc.selectFirst(".next") != null && doc.selectFirst(".next.over") == null)
    }

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl)

    override fun popularMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".comic_all li").map { node -> SManga.create().apply {
            title = node.selectFirst("a[title]").attr("title")
            thumbnail_url  = node.selectFirst("img").attr("xsrc")
            url = node.selectFirst("a").attr("href")
        } }
        return PagedList(ret, doc.selectFirst(".next") != null && doc.selectFirst(".next.over") == null)
    }

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga  = SManga.create().apply {
        Jsoup.parse(response.body()!!.string()).selectFirst(".comic_info").let {
            title = it.selectFirst("h1").text()
            thumbnail_url = it.selectFirst(".cover img").attr("src")
            author = it.selectFirst(".author_info .info a").text()
            status = when(it.selectFirst(".comic_infor_status span").text()) {
                "已完结" -> SManga.COMPLETED
                "连载中" -> SManga.ONGOING
                else -> SManga.UNKNOWN
            }
            genre = it.select(".class_tag")
                    .map{ node -> node.text() }.joinToString(", ")
            description = it.selectFirst(".words").text()
        }
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select("#chapter li").map { node -> SChapter.create().apply {
            name = when(node.selectFirst("a").className()){
                "vip_chapter" -> "🔒" + node.text()
                else -> node.text()
            }
            url = node.selectFirst("a").attr("href")
        } }
        return PagedList(ret.reversed(), false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request = GET(chapter.url!!)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val res = response.body()!!.string()
        val list = JSONObject(Regex("image_list\\s*:\\s*(.*?\\}\\})").find(res)!!.groupValues[1])
        val ret = ArrayList<MangaPage>()
        var i = 0
        list.keys().forEach {
            ret.add(MangaPage(i, "", String(Base64.decode(list.getJSONObject(it).getString("src"), Base64.NO_WRAP))))
            i++
        }
        return ret
    }

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun getFilterList(): FilterList = FilterList()
}