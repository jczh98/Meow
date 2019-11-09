package top.rechinx.meow.core.source.internal

import okhttp3.Request
import okhttp3.Response
import org.jsoup.Jsoup
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*
import java.net.URI
import java.util.ArrayList

class DongMan:HttpSource() {
    override val name = "咚漫"
    override val baseUrl = "https://www.dongmanmanhua.cn"

    private fun GET(url: String) = Request.Builder()
            .url(URI(baseUrl).resolve(url).toString())
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request
            = GET("$baseUrl/search?keyword=$keyword&page=$page")
    private fun commonMangaParse(response: Response): PagedList<SManga> {
        val doc = Jsoup.parse(response.body()!!.string())
        val ret = doc.select(".card_lst li").map { node -> SManga.create().apply {
            title = node.selectFirst(".subj").text()
            thumbnail_url  = node.selectFirst("img").attr("src")
            url = node.selectFirst("a").attr("href")
            author = node.selectFirst(".author").text()
            genre = node.selectFirst(".genre").text()
        } }
        return PagedList(ret, true)
    }
    override fun searchMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    override fun popularMangaRequest(page: Int): Request = GET(baseUrl)

    override fun popularMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga = SManga.create().apply {
        Jsoup.parse(response.body()!!.string()).let {
            title = it.selectFirst("h1").text()
            author = it.selectFirst(".author").text()
            thumbnail_url  = it.selectFirst(".other_card img").attr("src")
            genre = it.selectFirst(".genre").text().replace(" ",", ")
            description = it.selectFirst(".summary").text()
        }
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        Jsoup.parse(response.body()!!.string()).let{
            val href = it.selectFirst("#_listUl a").attr("href")
            val split = href.lastIndexOf("=") + 1
            val half = href.substring(0,split)
            val ret = ArrayList<SChapter>()
            for(i in href.substring(split).toInt() downTo 1){
                ret.add(SChapter.create().apply {
                    name = i.toString()
                    url = half + i
                })
            }
            return PagedList(ret, false)
        }
    }

    override fun mangaPagesRequest(chapter: SChapter): Request = GET(chapter.url!!)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val doc = Jsoup.parse(response.body()!!.string())
        return doc.select("#_viewerBox img").mapIndexed { i, node ->
            MangaPage(i, "", node.attr("data-url"))
        }
    }

    override fun headersBuilder() = super.headersBuilder().add("Referer", baseUrl)!!

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun getFilterList(): FilterList = FilterList()
}