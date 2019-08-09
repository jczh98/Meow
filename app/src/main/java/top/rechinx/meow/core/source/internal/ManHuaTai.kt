package top.rechinx.meow.core.source.internal

import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*

class ManHuaTai:HttpSource() {
    override val name = "漫画台"
    override val baseUrl = "http://getcomicinfo-globalapi.yyhao.com"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request
            = GET("$baseUrl/app_api/v5/getsortlist/?search_key=$keyword&page=$page")

    private fun commonMangaParse(response: Response): PagedList<SManga>{
        val json = JSONObject(response.body()!!.string())
        val data = json.getJSONArray("data")
        val ret = ArrayList<SManga>()
        for(i in 0 until data.length()){
            ret.add(SManga.create().apply {
                val manga = data.getJSONObject(i)
                val id = manga.getInt("comic_id").toString()
                title = manga.getString("comic_name")
                thumbnail_url = "http://image.mhxk.com/mh/$id.jpg"
                genre = manga.getString("comic_type")
                url = "$baseUrl/app_api/v5/getcomicinfo_body/?comic_id=$id"
            })
        }
        return PagedList(ret, true)
    }

    override fun searchMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    override fun popularMangaRequest(page: Int): Request
            = GET("$baseUrl/app_api/v5/getsortlist/?page=$page")

    override fun popularMangaParse(response: Response): PagedList<SManga>  = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga  = SManga.create().apply {
        JSONObject(response.body()!!.string()).let {
            title = it.getString("comic_name")
            thumbnail_url = it.getJSONArray("cover_list").getString(0)
            author = it.getString("comic_author")
            status = when(it.getInt("comic_status")) {
                0 -> SManga.COMPLETED
                1 -> SManga.ONGOING
                else -> SManga.UNKNOWN
            }

            val types = it.getJSONObject("comic_type")
            val tmp = ArrayList<String>()
            for (key in types.keys()) {
                tmp.add(types.getString(key))
            }
            genre = tmp.joinToString(", ")

            description = it.getString("comic_desc")
        }
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val json = JSONObject(response.body()!!.string())
        val chapters = json.getJSONArray("comic_chapter")
        val ret = ArrayList<SChapter>()
        for(i in 0 until chapters.length()){
            val chapter =  chapters.getJSONObject(i)
            ret.add(SChapter.create().apply {
                name = when(chapter.has("isbuy") && chapter.getInt("isbuy") == 1) {
                    true -> "💰"
                    else -> ""
                } + when(chapter.getInt("islock")) {
                    1 -> "🔒"
                    else -> ""
                } + chapter.getString("chapter_name")

                url = response.request().url().toString()+"&chapter="+i.toString()
                chapter_number = i.toString()
                date_updated = 1000 * chapter.getLong("create_date")
            })
        }
        return PagedList(ret, false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request = GET(chapter.url!!)

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        JSONObject(response.body()!!.string())
                .getJSONArray("comic_chapter")
                .getJSONObject(response.request().url().queryParameter("chapter")!!.toInt())
                .let{
                    val rule = it.getJSONObject("chapter_image").getString("high")
                    val ext = rule.split("$$")[1]
                    val server = "https://mhpic."+it.getString("chapter_domain")+rule.split("$$")[0]
                    val ret = ArrayList<MangaPage>()
                    for(i in 1 .. it.getInt("end_num")){
                        ret.add(MangaPage(i, "", server+i.toString()+ext))
                    }
                    return ret
                }
    }

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun getFilterList(): FilterList = FilterList()
}