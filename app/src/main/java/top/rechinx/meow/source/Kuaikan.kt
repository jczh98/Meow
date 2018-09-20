//package top.rechinx.meow.source
//
//import com.bumptech.glide.load.model.GlideUrl
//import com.bumptech.glide.load.model.LazyHeaders
//import okhttp3.Request
//import org.json.JSONArray
//import org.json.JSONException
//import org.json.JSONObject
//import top.rechinx.meow.core.JsonIterator
//import top.rechinx.meow.core.Parser
//import top.rechinx.meow.core.SearchIterator
//import top.rechinx.meow.manager.SourceManager
//import top.rechinx.meow.model.Chapter
//import top.rechinx.meow.model.Comic
//import top.rechinx.meow.model.ImageUrl
//import top.rechinx.meow.model.Source
//import top.rechinx.meow.soup.Node
//import top.rechinx.meow.support.relog.ReLog
//import java.util.*
//
//class Kuaikan(source: Source): Parser() {
//
//    init {
//        init(source)
//    }
//
//    override fun getSearchRequest(keyword: String, page: Int): Request? {
//        if(page > 1) return null
//        val url = "http://search.kkmh.com/search/complex?q=$keyword"
//        return Request.Builder()
//                .url(url)
//                .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)")
//                .build()
//    }
//
//    override fun getInfoRequest(cid: String, page: Int): Request? {
//        if(page > 1) return null
//        val url = "http://www.kuaikanmanhua.com/web/topic/$cid"
//        return Request.Builder()
//                .url(url)
//                .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)")
//                .build()
//    }
//
//    override fun parserInto(html: String, comic: Comic) {
//        val body = Node(html)
//        val title = body.text("div.comic-name")
//        val cover = body.src("div.comic-img > img")
//        val update = "2018" // todo
//        val intro = body.text("div.switch-content > p")
//        val author = body.text("div.author-nickname")
//        val glideCover = GlideUrl(cover, LazyHeaders.Builder().build())
//        //comic.setInfo(title, cover, update, intro!!, author, false, false, glideCover)
//    }
//
//    override fun getSearchIterator(html: String, page: Int): SearchIterator? {
//        try {
//            val jsonObject = JSONObject(html)
//            val jsonData = jsonObject.getJSONObject("data")
//            val jsonPost = jsonData.getJSONObject("topic")
//            val jsonHit = jsonPost.getJSONArray("hit")
//            return object : JsonIterator(jsonHit) {
//                override fun parse(obj: JSONObject): Comic? {
//                    try {
//                        val user = obj.getJSONObject("user")
//                        val cid = obj.getLong("id").toString()
//                        val title = obj.getString("title")
//                        val cover = obj.getString("vertical_image_url")
//                        val author = user.getString("nickname")
//                        val update = obj.getString("latest_comic_title")
//                        val glideCover = GlideUrl(cover, LazyHeaders.Builder().build())
//                        return null
//                        //return Comic(TYPE, cid, title, cover, author, update, glideCover)
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        return null
//                    }
//
//                }
//            }
//        } catch (e: JSONException) {
//            e.printStackTrace()
//            return null
//        }
//    }
//
//    override fun getImageRequest(cid: String, image: String): Request? {
//        val url = "http://api.kuaikanmanhua.com/v2/comic/$image"
//        return Request.Builder()
//                .url(url)
//                .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)")
//                .build()
//    }
//
//    override fun parseImage(html: String): List<ImageUrl>? {
//        val list = LinkedList<ImageUrl>()
//        try {
//            val obj = JSONObject(html)
//            val jsonData = obj.getJSONObject("data")
//            val array = jsonData.getJSONArray("images")
//            for (i in 0 until array.length()) {
//                val glideUrl = GlideUrl(array.getString(i), LazyHeaders.Builder()
//                        .addHeader("Referer", "http://images.dmzj.com/")
//                        .build())
//                list.add(ImageUrl(i + 1, glideUrl))
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//        }
//
//        return list
//    }
//
//    override fun parseChapter(html: String): List<Chapter>? {
//        val list = LinkedList<Chapter>()
//        val body = Node(html)
//        val array = body.list("div.article-list > table > tbody > tr > td.tit > a")
//        for(index in array.indices) {
//            val chapterString = array[index].attr("href")
//            val chapterId = chapterString?.split("/")?.get(3)
//            val title = array[index].attr("title")
//            list.add(Chapter(title!!, chapterId!!))
//        }
//        return list
//    }
//
//    override fun constructCoverGlideUrl(url: String): GlideUrl? = GlideUrl(url, LazyHeaders.Builder().build())
//
//    override fun constructPicGlideUrl(url: String): GlideUrl? = GlideUrl(url, LazyHeaders.Builder().build())
//
//    companion object {
//
//        const val TYPE = 3
//
//        const val DEFAULT_TITLE = "快看漫画"
//
//        fun getDefaultSource(): Source = SourceManager.getInstance().identify(TYPE, DEFAULT_TITLE)
//    }
//}