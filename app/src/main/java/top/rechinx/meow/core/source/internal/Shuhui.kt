package top.rechinx.meow.core.source.internal

import okhttp3.Headers
import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import org.jsoup.Jsoup
import org.jsoup.nodes.Document
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*
import top.rechinx.meow.support.log.L

class Shuhui: HttpSource() {
    override val baseUrl: String
        get() = "http://www.ishuhui.net"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/56.0.2924.87 ")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request {
        return GET("http://www.ishuhui.net/ComicBooks/GetAllBook?Title=$keyword&PageIndex=${page-1}")
    }

    override fun searchMangaParse(response: Response): PagedList<SManga> {
        return commonParse(response)
    }

    private fun commonParse(response: Response): PagedList<SManga> {
        val obj = JSONObject(response.body()!!.string())
        val arr = obj.getJSONObject("Return").getJSONArray("List")
        val ret = ArrayList<SManga>(arr.length())
        for(i in 0 until arr.length()) {
            val item = arr.getJSONObject(i)
            var cid = item.getString("Id")
            ret.add(SManga.create().apply {
                title = item.getString("Title")
                thumbnail_url = item.getString("FrontCover")
                author = item.optString("Author")
                status = SManga.UNKNOWN
                this.cid = cid
            })
        }
        return PagedList(ret, arr.length() != 0)
    }

    override fun popularMangaRequest(page: Int): Request {
        return GET("http://www.ishuhui.net/ComicBooks/GetAllBook?PageIndex=${page-1}")
    }

    override fun popularMangaParse(response: Response): PagedList<SManga> {
        return commonParse(response)
    }

    override fun mangaInfoRequest(cid: String): Request {
        return GET("http://www.ishuhui.net/ComicBooks/GetChapterList?id=$cid")
    }

    override fun mangaInfoParse(response: Response): SManga = SManga.create().apply{
        val obj = JSONObject(response.body()!!.string()).getJSONObject("Return").getJSONObject("ParentItem")

        title = obj.getString("Title")
        thumbnail_url = obj.getString("FrontCover")

        author = obj.getString("Author")

        genre = ""
        status = SManga.UNKNOWN
        description = obj.getString("Explain")
    }

    override fun chaptersRequest(page: Int, cid: String): Request {
        return GET("http://www.ishuhui.net/ComicBooks/GetChapterList?id=$cid&PageIndex=${page-1}")
    }

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val obj = JSONObject(response.body()!!.string()).getJSONObject("Return")
        val ret = ArrayList<SChapter>()
        val arr = obj.getJSONArray("List")
        for (i in 0 until arr.length()) {
            val chapter = arr.getJSONObject(i)
            ret.add(SChapter.create().apply {
                name = "${chapter.getString("Title")}"
                date_updated = 0
                url = "http://www.ishuhui.net/ComicBooks/ReadComicBooksToIsoV1/${chapter.getString("Id")}.html"
            })
        }
        return PagedList(ret, arr.length() != 0)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request {
        return GET(chapter.url!!)
    }

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val document = response.asJsoup()
        val ret = ArrayList<MangaPage>()
        val pages = document.select("img[src]").mapIndexed { index, element ->
            ret.add(MangaPage(index, "", element.attr("src")))
        }
        return ret
    }

    override val name: String
        get() = "鼠绘"

    override fun getFilterList(): FilterList {
        return FilterList()
    }

    fun Response.asJsoup(html: String? = null): Document {
        return Jsoup.parse(html ?: body()!!.string(), request().url().toString())
    }
}