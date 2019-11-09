package top.rechinx.meow.core.source.internal

import okhttp3.Request
import okhttp3.Response

import org.jsoup.Jsoup

import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*


class ManHuaDui:HttpSource() {

    override val name: String = "漫画堆"

    override val baseUrl: String = "https://m.manhuadui.com"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Linux; Android 6.0; Nexus 5 Build/MRA58N) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/78.0.3904.87 Mobile Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request
            = GET("https://m.manhuadui.com/search/?keywords=$keyword&page=$page")

    override fun searchMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    private fun commonMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".itemBox,.list-comic").map { node -> SManga.create().apply {
            title = node.selectFirst(".title").text()
            thumbnail_url  = node.selectFirst("img").attr("src")
            url = node.selectFirst(".title").attr("href")
            description = node.selectFirst(".pd,.date").text()
            author = node.selectFirst(".txtItme").text()
        } }
        return PagedList(ret, true)
    }

    override fun popularMangaRequest(page: Int): Request = GET("https://m.manhuadui.com/update/$page/")

    override fun popularMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga = SManga.create().apply {
        val dom = Jsoup.parse(response.body()!!.string())
        title = dom.selectFirst("h1").text()
        thumbnail_url = dom.selectFirst("#Cover img").attr("src")
        author = dom.selectFirst(".txtItme").text().substringAfter("：").trim()
//        status = when(infoElement.selectFirst("span:nth-of-type(1) span").text()) {
//            "连载中" -> SManga.ONGOING
//            "完结" -> SManga.COMPLETED
//            else -> SManga.UNKNOWN
//        }
//        genre = infoElement.select(".tip a")
//                .map{ node -> node.text() }.joinToString(", ")
        description = dom.select("#full-des").text()
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select("#chapter-list-1 a").map { node -> SChapter.create().apply {
            name = node.text()
            url = "https://m.manhuadui.com${node.attr("href")}"
        } }
        return PagedList(ret.reversed(), false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request = GET(chapter.url!!)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val doc = Jsoup.parse(response.body()!!.string())
        val info = doc.select(".BarTit").text()
        val reg = Regex("\\d+/(\\d+)").find(info)
        if(reg != null){
            val len = reg.groupValues[1].toInt()
            val ret = ArrayList<MangaPage>(len)
            val sp = response.request().url().url().toString().split(".html")
            for ( i in 1 .. len){
                ret.add(MangaPage(i, "${sp[0]}-$i.html", ""))
            }
            return ret
        }
        return ArrayList<MangaPage>()
    }

    override fun imageUrlParse(response: Response): String
            = Jsoup.parse(response.body()!!.string()).select("mip-img").attr("src")

    override fun getFilterList(): FilterList = FilterList()

}