package top.rechinx.meow.core.source.internal

import okhttp3.*
import org.json.JSONObject
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.*
import java.util.zip.ZipInputStream
import kotlin.experimental.xor

class Bilibili:HttpSource() {

    override val name = "Bilibili"
    override val baseUrl = "https://manga.bilibili.com"
    private val cleanRegex = Regex("</?em.*?>")

    private fun POST(url: String, json:String) = Request.Builder()
            .url(url)
            .post(RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json))
            .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
            .build()

    override fun searchMangaRequest(keyword: String, page: Int, filters: FilterList): Request {
        if("" != keyword){
            return POST("$baseUrl/twirp/comic.v1.Comic/Search?device=pc&platform=web",
                    "{\"key_word\":\"$keyword\",\"page_num\":$page,\"page_size\":9}")
        } else{
            val json = filters.map { (it as UriPartFilter).getUrl() }.joinToString(",")
            return POST("$baseUrl/twirp/comic.v1.Comic/ClassPage?device=pc&platform=web",
                    "{$json,\"page_num\":$page,\"page_size\":18}")
        }
    }

    private fun commonMangaParse(response: Response): PagedList<SManga> {
        val json = JSONObject(response.body()!!.string())
        if(response.request().url().toString().contains("Search")){
            val data = json.getJSONObject("data")
            val arr = data.getJSONArray("list")
            val ret = ArrayList<SManga>()
            for (i in 0 until arr.length()) {
                val info = arr.getJSONObject(i)
                ret.add(SManga.create().apply {
                    title = info.getString("title").replace(cleanRegex, "")
                    thumbnail_url  = info.getString("vertical_cover")
                    url = info.getInt("id").toString()

                    val tmp = ArrayList<String>()
                    val styles = info.getJSONArray("styles")
                    for (j in 0 until styles.length()) {
                        tmp.add(styles.getString(j))
                    }
                    genre = tmp.joinToString(", ")

                    tmp.clear()
                    val authors = info.getJSONArray("author_name")
                    for (j in 0 until authors.length()) {
                        tmp.add(authors.getString(j))
                    }
                    author = tmp.joinToString(", ").replace(cleanRegex, "")
                })
            }
            return PagedList(ret, true)
        }
        else{
            val arr = json.getJSONArray("data")
            val ret = ArrayList<SManga>(arr.length())
            for (i in 0 until arr.length()) {
                val info = arr.getJSONObject(i)
                ret.add(SManga.create().apply {
                    title = info.getString("title")
                    thumbnail_url  = info.getString("vertical_cover")
                    url = info.getInt("season_id").toString()
                })
            }
            return PagedList(ret, true)
        }
    }

    override fun searchMangaParse(response: Response): PagedList<SManga>  = commonMangaParse(response)

    override fun popularMangaRequest(page: Int): Request =  POST("$baseUrl/twirp/comic.v1.Comic/ClassPage?device=pc&platform=web",
            "{\"style_id\":-1,\"area_id\":-1,\"is_finish\":-1,\"order\":0,\"page_num\":$page,\"page_size\":18,\"is_free\":-1}")

    override fun popularMangaParse(response: Response): PagedList<SManga>  = commonMangaParse(response)

    override fun mangaInfoRequest(url: String): Request = POST("$baseUrl/twirp/comic.v2.Comic/ComicDetail?device=pc&platform=web","{\"comic_id\":$url}")

    override fun mangaInfoParse(response: Response): SManga = SManga.create().apply {
        val json = JSONObject(response.body()!!.string())
        val data = json.getJSONObject("data")
        title = data.getString("title")
        thumbnail_url = data.getString("vertical_cover")
        url = data.getInt("id").toString()

        val tmp = ArrayList<String>()
        val authors = data.getJSONArray("author_name")
        for (j in 0 until authors.length()) {
            tmp.add(authors.getString(j))
        }
        author = tmp.joinToString(", ")

        status = when(data.getInt("is_finish")) {
            1 -> SManga.COMPLETED
            0 -> SManga.ONGOING
            else -> SManga.UNKNOWN
        }

        tmp.clear()
        val styles = data.getJSONArray("styles")
        for (j in 0 until styles.length()) {
            tmp.add(styles.getString(j))
        }
        genre = tmp.joinToString(", ")

        description = data.getString("evaluate")
    }

    override fun chaptersRequest(page: Int, url: String): Request = POST("$baseUrl/twirp/comic.v2.Comic/ComicDetail?device=pc&platform=web","{\"comic_id\":$url}")

    override fun chaptersParse(response: Response): PagedList<SChapter> {
        val json = JSONObject(response.body()!!.string())
        val data = json.getJSONObject("data")
        val list = data.getJSONArray("ep_list")

        val ret = ArrayList<SChapter>()
        for (i in 0 until list.length()) {
            val chapter = list.getJSONObject(i)
            ret.add(SChapter.create().apply {
                name = (when(chapter.getBoolean("is_locked")){
                    true -> "🔒" + chapter.getString("short_title")
                    false -> chapter.getString("short_title")
                })
                url = chapter.getInt("id").toString()
            })
        }
        return PagedList(ret, false)
    }

    override fun mangaPagesRequest(chapter: SChapter): Request  = POST("$baseUrl/twirp/comic.v1.Comic/GetImageIndex?device=pc&platform=web","{\"ep_id\":${chapter.url}}")


    override fun mangaPagesParse(response: Response): List<MangaPage> {
        val json = JSONObject(response.body()!!.string())
        if("" != json.getString("msg")){
            val ret = ArrayList<MangaPage>()
            ret.add(MangaPage(0, "", "https://i0.hdslb.com/bfs/activity-plat/cover/20171017/496nrnmz9x.png"))
            return ret
        }

        val data = json.getJSONObject("data")
        val path = data.getString("path")
        var host = "https://i0.hdslb.com"
        if(data.has("host") && ""!=data.getString("host")){
            host = data.getString("host")
        }
        val m = Regex("^/bfs/manga/(\\d+)/(\\d+)").find(path)!!
        val cid = m.groupValues[1].toInt()
        val epid = m.groupValues[2].toInt()

        var bytes = client.newCall(Request.Builder()
                .url(host+path)
                .addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/75.0.3770.142 Safari/537.36")
                .build()).execute().body()!!.bytes()
        bytes = bytes.sliceArray(9 until bytes.size)

        val key = ByteArray(8)
        key[0] = epid.toByte()
        key[1] = epid.shr(8).toByte()
        key[2] = epid.shr(16).toByte()
        key[3] = epid.shr(24).toByte()
        key[4] = cid.toByte()
        key[5] = cid.shr(8).toByte()
        key[6] = cid.shr(16).toByte()
        key[7] = cid.shr(24).toByte()
        for (i in 0 until bytes.size){
            bytes[i] = bytes[i].xor(key[i % 8])
        }

        val zis = ZipInputStream(bytes.inputStream())
        zis.nextEntry
        val pics = JSONObject(zis.bufferedReader().use { it.readText() }).getJSONArray("pics")
        val tokens = JSONObject(client.newCall(POST("$baseUrl/twirp/comic.v1.Comic/ImageToken?device=pc&platform=web",
                "{\"urls\":\"${pics.toString().replace("\"","\\\"")}\"}"))
                .execute().body()!!.string()).getJSONArray("data")
        val ret = ArrayList<MangaPage>()
        for (i in 0 until tokens.length()) {
            val token = tokens.getJSONObject(i)
            ret.add(MangaPage(i, "", "${token.getString("url")}?token=${token.getString("token")}"))
        }
        return ret
    }

    override fun imageUrlParse(response: Response): String  = throw UnsupportedOperationException("Unused method was called somehow!")

    override fun getFilterList(): FilterList = FilterList(StylesFilter(), AreasFilter(), StatusFilter(), PricesFilter(), OrdersFilter())

    private open class UriPartFilter(displayName: String, val vals: Array<Pair<String, String>>,
                                     defaultValue: Int = 0) :
            Filter.Select<String>(displayName, vals.map { it.first }.toTypedArray(), defaultValue){
        open fun getUrl() = vals[state].second
    }

    private class StylesFilter : UriPartFilter("题材", arrayOf(
            Pair("全部", "\"style_id\":-1"),
            Pair("冒险", "\"style_id\":1013"),
            Pair("热血", "\"style_id\":999"),
            Pair("搞笑", "\"style_id\":994"),
            Pair("恋爱", "\"style_id\":995"),
            Pair("少女", "\"style_id\":1026"),
            Pair("日常", "\"style_id\":1020"),
            Pair("校园", "\"style_id\":1001"),
            Pair("运动", "\"style_id\":1010"),
            Pair("正能量", "\"style_id\":1028"),
            Pair("治愈", "\"style_id\":1007"),
            Pair("古风", "\"style_id\":997"),
            Pair("玄幻", "\"style_id\":1016"),
            Pair("奇幻", "\"style_id\":998"),
            Pair("惊奇", "\"style_id\":996"),
            Pair("悬疑", "\"style_id\":1023"),
            Pair("都市", "\"style_id\":1002"),
            Pair("总裁", "\"style_id\":1004")
    ))

    private class AreasFilter : UriPartFilter("地区", arrayOf(
            Pair("全部", "\"area_id\":-1"),
            Pair("大陆", "\"area_id\":1"),
            Pair("日本", "\"area_id\":2"),
            Pair("其他", "\"area_id\":5")
    ))

    private class StatusFilter : UriPartFilter("进度", arrayOf(
            Pair("全部", "\"is_finish\":-1"),
            Pair("连载", "\"is_finish\":0"),
            Pair("完结", "\"is_finish\":1"),
            Pair("新上架", "\"is_finish\":2")
    ))

    private class PricesFilter : UriPartFilter("收费", arrayOf(
            Pair("全部", "\"is_free\":-1"),
            Pair("免费", "\"is_free\":1"),
            Pair("付费", "\"is_free\":2")
    ))

    private class OrdersFilter : UriPartFilter("排序", arrayOf(
            Pair("人气推荐", "\"order\":0"),
            Pair("更新时间", "\"order\":1"),
            Pair("追漫人数", "\"order\":2")
    ))
}