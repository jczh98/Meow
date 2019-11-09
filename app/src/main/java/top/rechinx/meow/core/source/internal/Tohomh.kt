package top.rechinx.meowo.core.source.internal

import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.jsoup.Jsoup
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*
import java.text.SimpleDateFormat
import java.util.Locale

class Tohomh:HttpSource() {

    override val name = "Tohomh123"

    override val baseUrl = "https://www.tohomh123.com"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request
            = GET("$baseUrl/action/Search?keyword=$keyword&page=$page")

    override fun searchMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    private fun commonMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select("div.mh-item").map { node -> SManga.create().apply {
            title = node.selectFirst("h2 a").text()
            thumbnail_url  = node.selectFirst("p.mh-cover").attr("style").substringAfter("(").substringBefore(")")
            url = baseUrl + node.selectFirst("h2 a").attr("href")
        } }
        return PagedList(ret, doc.selectFirst("div.page-pagination a:contains(>)") != null)
    }

    override fun popularMangaRequest(page: Int): Request = GET("$baseUrl/f-1-------hits--$page.html")

    override fun popularMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga = SManga.create().apply {
        val infoElement = Jsoup.parse(response.body()!!.string()).selectFirst(".banner_detail_form")
        title = infoElement.selectFirst("h1").text()
        thumbnail_url = infoElement.selectFirst(".cover img").attr("src")
        author = infoElement.selectFirst(".subtitle").text().substringAfter("：").trim()
        status = when(infoElement.selectFirst("span:nth-of-type(1) span").text()) {
            "连载中" -> SManga.ONGOING
            "完结" -> SManga.COMPLETED
            else -> SManga.UNKNOWN
        }
        genre = infoElement.select(".tip a")
                .map{ node -> node.text() }.joinToString(", ")
        description = infoElement.select("p.content").text()
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select("ul#detail-list-select-1 a").map { node -> SChapter.create().apply {
            name = node.text()
            url = node.attr("href")
        } }
        doc.select(".banner_detail_form span:nth-of-type(3)").text()
                .substringAfter("：").trim().let {
                    ret[0].date_updated = SimpleDateFormat("yyyy-MM-dd", Locale.CHINA).parse(it).time
                }
        return PagedList(ret, false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request = GET(baseUrl + chapter.url)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val doc = Jsoup.parse(response.body()!!.string())
        val script = doc.select("script:containsData(did)").first().data()
        val did = script.substringAfter("did=").substringBefore(";")
        val sid = script.substringAfter("sid=").substringBefore(";")
        val lastPage = script.substringAfter("pcount =").substringBefore(";").trim().toInt()
        val ret = ArrayList<MangaPage>(lastPage)
        for (i in 1..lastPage) {
            ret.add(MangaPage(i, "$baseUrl/action/play/read?did=$did&sid=$sid&iid=$i", ""))
        }
        return ret
    }

    override fun imageUrlParse(response: Response): String
            = JSONObject(response.body()!!.string()).getString("Code")

    override fun getFilterList(): FilterList = FilterList()

}