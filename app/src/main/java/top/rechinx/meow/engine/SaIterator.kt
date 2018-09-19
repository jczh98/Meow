package top.rechinx.meow.engine

import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import top.rechinx.meow.model.Comic
import top.rechinx.meow.source.Dmzj

class SaIterator(val array: JSONArray) {

    private var index: Int = 0

    init {
        this.index = 0
    }

    fun hasNext(): Boolean {
        return index < array.length()
    }

    fun next(): Comic? {
        try {
            return parse(array.getJSONObject(index++))
        } catch (e: JSONException) {
            return null
        }
    }

    fun empty(): Boolean {
        return array == null || array.length() == 0
    }

    fun parse(obj: JSONObject): Comic? {
        return try {
            val cid = obj.getString("cid")
            val title = obj.getString("title")
            val cover = obj.getString("cover")
            val author = obj.getString("author")
            val update = obj.getString("update")
            Comic(cid, title, cover, author, update)
        }catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }
}