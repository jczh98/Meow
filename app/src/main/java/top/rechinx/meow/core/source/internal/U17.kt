package top.rechinx.meow.core.source.internal

import okhttp3.Request
import okhttp3.Response
import org.json.JSONObject
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*

class U17:HttpSource() {
    override val name = "U17"
    override val baseUrl = "http://app.u17.com"

    private fun GET(url: String) = Request.Builder()
            .url(url)
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request
            = GET("$baseUrl/v3/appV3_3/android/phone/search/searchResult?q=$keyword&page=$page")

    private fun commonMangaParse(response: Response): PagedList<SManga>{
        val json = JSONObject(response.body()!!.string())
        val data = json.getJSONObject("data").getJSONObject("returnData")
        val comics = data.getJSONArray("comics")
        val ret = ArrayList<SManga>()
        for(i in 0 until comics.length()){
            ret.add(SManga.create().apply {
                val manga = comics.getJSONObject(i)
                title = manga.getString("name")
                author = manga.getString("author")
                thumbnail_url = manga.getString("cover")

                genre = manga.getJSONArray("tags").join(", ")

                url = when(manga.has("comicId")){
                    true -> "$baseUrl/v3/appV3_3/android/phone/comic/detail_static_new?&comicid=${manga.getInt("comicId")}"
                    else -> "$baseUrl/v3/appV3_3/android/phone/comic/detail_static_new?&comicid=${manga.getInt("comic_id")}"
                }

                description = manga.getString("description")
            })
        }
        return PagedList(ret, data.has("hasMore") && data.getBoolean("hasMore"))
    }

    override fun searchMangaParse(response: Response): PagedList<SManga> = commonMangaParse(response)

    override fun popularMangaRequest(page: Int): Request
            = GET("$baseUrl/v3/appV3_3/android/phone/list/conditionScreenlists?page=$page")

    override fun popularMangaParse(response: Response): PagedList<SManga>  = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request = GET(url)

    override fun mangaInfoParse(response: Response): SManga  = SManga.create().apply {
        JSONObject(response.body()!!.string())
                .getJSONObject("data")
                .getJSONObject("returnData")
                .getJSONObject("comic")
                .let {
                    title = it.getString("name")
                    thumbnail_url = it.getString("cover")
                    author = it.getJSONObject("author").getString("name")
                    status = when(it.getInt("series_status")) {
                        1 -> SManga.COMPLETED
                        0 -> SManga.ONGOING
                        else -> SManga.UNKNOWN
                    }

                    genre = (it.getJSONArray("tagList").join(", ") + ", "
                            + it.getJSONArray("theme_ids").join(", ")).replace("\"","")

                    description = it.getString("description")
        }
    }

    override fun chaptersRequest(page: Int, url: String): Request = GET(url)

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        JSONObject(response.body()!!.string())
                .getJSONObject("data")
                .getJSONObject("returnData")
                .getJSONArray("chapter_list")
                .let {
                    val ret = ArrayList<SChapter>()
                    for(i in it.length() downTo 0){
                        val chapter =  it.getJSONObject(i)
                        ret.add(SChapter.create().apply {
                            name = when(chapter.getInt("type")) {
                                2 ->  "🔒"
                                3 -> "🔓"
                                else -> ""
                            } + chapter.getString("name")
                            url = chapter.getString("chapter_id")
                            chapter_number = chapter.getString("index")
                            date_updated = 1000 * chapter.getLong("pass_time")
                        })
                    }
                    return PagedList(ret, false)
                }
    }

    override fun mangaPagesRequest(chapter: SChapter): Request
            = GET("$baseUrl/v3/appV3_3/android/phone/comic/chapterNew?chapter_id=${chapter.url}")

    override fun mangaPagesParse(response: Response): List<MangaPage> {
        JSONObject(response.body()!!.string())
                .getJSONObject("data")
                .getJSONObject("returnData")
                .let{
                    val ret = ArrayList<MangaPage>()

                    if(it.has("image_list")){
                        val images = it.getJSONArray("image_list")
                        for(i in 0 until images.length()){
                            ret.add(MangaPage(i, "", images.getJSONObject(i).getString("location")))
                        }
                    }
                    if(it.has("free_image_list")){
                        val images = it.getJSONArray("free_image_list")
                        for(i in 0 until images.length()){
                            ret.add(MangaPage(i, "", images.getJSONObject(i).getString("location")))
                        }
                    }
                    /*if(it.has("unlock_image")){
                        val images = it.getJSONArray("unlock_image")
                        for(i in 0 until images.length()){
                            ret.add(MangaPage(i, "", images.getJSONObject(i).getString("blur_image_url")))
                        }
                    }*/

                    return ret
                }
    }

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun getFilterList(): FilterList = FilterList()
}