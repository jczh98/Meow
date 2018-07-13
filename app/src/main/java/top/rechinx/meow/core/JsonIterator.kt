package top.rechinx.meow.core

import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import top.rechinx.meow.model.Comic

abstract class JsonIterator(val array: JSONArray) : SearchIterator {

    private var index: Int = 0

    init {
        this.index = 0
    }

    override fun hasNext(): Boolean {
        return index < array.length()
    }

    override fun next(): Comic? {
        try {
            return parse(array.getJSONObject(index++))
        } catch (e: JSONException) {
            return null
        }
    }

    override fun empty(): Boolean {
        return array == null || array.length() == 0
    }

    protected abstract fun parse(obj: JSONObject): Comic?

}