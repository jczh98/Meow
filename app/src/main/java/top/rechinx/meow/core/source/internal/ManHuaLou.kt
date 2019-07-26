package top.rechinx.meow.core.source.internal

import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray

import org.jsoup.Jsoup

import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*


class ManHuaLou:HttpSource() {

    override val name: String = "漫画楼"

    override val baseUrl: String = "https://www.manhualou.com"


    override fun getFilterList(): FilterList {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun imageUrlParse(response: Response): String {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request
            = GET("$baseUrl/search/?keywords=$keyword&page=$page")

    override fun searchMangaParse(response: Response): PagedList<SManga>
            = commonMangaParse(response)

    private fun commonMangaParse(response: Response): PagedList<SManga> {
        val res = response.body()!!.string()
        val ret = Jsoup.parse(res).select("#contList li").map { node -> SManga.create().apply {
            title = node.selectFirst("p").text()
            thumbnail_url  = node.selectFirst("img").attr("src")
            url = node.selectFirst("a").attr("href")
        } }
        return PagedList(ret, true)
    }
    override fun popularMangaRequest(page: Int): Request
            = GET("$baseUrl/list_$page")

    override fun popularMangaParse(response: Response): PagedList<SManga>
            = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request
            = GET(url)

    override fun mangaInfoParse(response: Response): SManga
            = SManga.create().apply {
        val res = response.body()!!.string()
        var doc = Jsoup.parse(res)
        title = doc.selectFirst(".book-title").text()
        thumbnail_url = doc.selectFirst(".cover .pic").attr("src")
        author = doc.selectFirst("ul.detail-list.cf > li:nth-of-type(2) > span:nth-of-type(2) > a").text()
        status = when(doc.selectFirst(".status a").text()) {
            "已完结" -> SManga.COMPLETED
            "连载中" -> SManga.ONGOING
            else -> SManga.UNKNOWN
        }
        description = doc.select(".book-intro").last().text()
    }

    override fun chaptersRequest(page: Int, url: String): Request
            = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val res = response.body()!!.string()
        val ret = Jsoup.parse(res).select(".chapter-body.clearfix a").map { node -> SChapter.create().apply {
            name = node.text()
            url = node.attr("href")
        } }
        return PagedList(ret, false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request
            = GET(baseUrl + chapter.url)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val res = response.body()!!.string()
        val arr = JSONArray(Regex("chapterImages\\s*=\\s*([^;]*)").find(res)!!.groupValues[1])
        val ret = ArrayList<MangaPage>(arr.length())
        for (i in 0 until arr.length()) {
            ret.add(MangaPage(i, "", "https://restp.dongqiniqin.com/"+arr.getString(i)))
        }
        return ret
    }


}