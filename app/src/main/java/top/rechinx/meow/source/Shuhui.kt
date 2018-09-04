package top.rechinx.meow.source

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import okhttp3.Request
import org.json.JSONException
import org.json.JSONObject
import top.rechinx.meow.core.JsonIterator
import top.rechinx.meow.core.Parser
import top.rechinx.meow.core.SearchIterator
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.model.Source
import top.rechinx.meow.soup.Node
import java.util.*

class Shuhui(source: Source) : Parser() {

    init {
        init(source)
    }

    override fun getSearchRequest(keyword: String, page: Int): Request? {
        if (page > 1) return null
        val url = "http://www.ishuhui.net/ComicBooks/GetAllBook?Title=$keyword"
        return Request.Builder()
                .url(url)
                .build()
    }

    override fun getInfoRequest(cid: String, page: Int): Request? {
        val url = "http://www.ishuhui.net/ComicBooks/GetChapterList?id=$cid&PageIndex=${page - 1}"
        return Request.Builder().url(url).build()
    }

    override fun parserInto(html: String, comic: Comic) {
        try {
            val obj = JSONObject(html).getJSONObject("Return").getJSONObject("ParentItem")
            val title = obj.getString("Title")
            val cover = obj.getString("FrontCover")
            val update = obj.getString("RefreshTimeStr")
            val intro = obj.optString("Explain")
            val author = obj.getString("Author")
            val glideCover = GlideUrl(cover, LazyHeaders.Builder().build())
            comic.setInfo(title, cover, update.toString(), intro, author, false, glideCover)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getSearchIterator(html: String, page: Int): SearchIterator? {
        try {
            val jsonObject = JSONObject(html)
            val jsonObject2 = jsonObject.getJSONObject("Return")
            val array = jsonObject2.getJSONArray("List")
            return object : JsonIterator(array) {
                override fun parse(obj: JSONObject): Comic? {
                    try {
                        val cid = obj.getString("Id")
                        val title = obj.getString("Title")
                        val cover = obj.getString("FrontCover")
                        val author = obj.getString("Author")
                        val update = obj.getString("RefreshTimeStr")
                        val glideCover = GlideUrl(cover, LazyHeaders.Builder().build())
                        return Comic(TYPE, cid, title, cover, author, update, glideCover)
                    } catch (e: Exception) {
                        e.printStackTrace()
                        return null
                    }
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
            return null
        }
    }

    override fun getImageRequest(cid: String, image: String): Request? {
        val url = "http://www.ishuhui.net/ComicBooks/ReadComicBooksToIsoV1/$image.html"
        return Request.Builder()
                .url(url)
                .build()
    }

    override fun parseImage(html: String): List<ImageUrl>? {
        val list = LinkedList<ImageUrl>()
        val body = Node(html)
        val array = body.list("img[src]")
        for(index in array.indices) {
            val url = array[index].src()
            val glideUrl = GlideUrl(url, LazyHeaders.Builder().build())
            list.add(ImageUrl(index + 1, glideUrl))
        }
        return list
    }

    override fun parseChapter(html: String): List<Chapter>? {
        val list = LinkedList<Chapter>()
        try {
            val obj = JSONObject(html).getJSONObject("Return")
            val array = obj.getJSONArray("List")
            for (i in 0 until array.length()) {
                val chapter = array.getJSONObject(i)
                val title = "${chapter.getInt("Sort")} ${chapter.getString("Title")}"
                val chapterId = chapter.getString("Id")
                list.add(Chapter(title, chapterId))
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return list
    }

    override fun constructCoverGlideUrl(url: String): GlideUrl? {
        return GlideUrl(url, LazyHeaders.Builder()
                .build())
    }

    override fun constructPicGlideUrl(url: String): GlideUrl? {
        return GlideUrl(url, LazyHeaders.Builder()
                .build())
    }

    companion object {

        const val TYPE = 2

        const val DEFAULT_TITLE = "鼠绘"

        fun getDefaultSource(): Source = SourceManager.getInstance().identify(TYPE, DEFAULT_TITLE)
    }
}