package top.rechinx.meow.source

import okhttp3.Request
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import top.rechinx.meow.core.JsonIterator
import top.rechinx.meow.core.Parser
import top.rechinx.meow.core.SearchIterator
import top.rechinx.meow.model.Comic
import top.rechinx.meow.support.relog.ReLog

open class Dmzj: Parser {

    override fun getSearchRequest(keyword: String, page: Int): Request? {
        if(page == 1) {
            val url = "http://v2.api.dmzj.com/search/show/0/$keyword/${page - 1}.json"
            return Request.Builder()
                    .url(url)
                    .header("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1; SV1; AcooBrowser; .NET CLR 1.1.4322; .NET CLR 2.0.50727)")
                    .build()
        }
        return null
    }

    override fun getInfoRequest(cid: String): Request? {
        return null
    }

    override fun parserInto(html: String): Comic? {
        return null
    }

    override fun getSearchIterator(html: String, page: Int): SearchIterator? {
        try {
            return object : JsonIterator(JSONArray(html)) {
                override fun parse(obj: JSONObject): Comic? {
                    try {
                        val cid = obj.getString("id")
                        val title = obj.getString("title")
                        val cover = obj.getString("cover")
                        val author = obj.optString("authors")
                        return Comic(SOURCE_DMZJ, cid, title, cover, author)
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    return null
                }
            }
        } catch (e: JSONException) {
            e.printStackTrace()
        }
        return null
    }

    companion object {
        val SOURCE_DMZJ = 1
    }
}