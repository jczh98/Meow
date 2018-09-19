package top.rechinx.meow.engine

class SaAttrList {

    private var mAttrs = HashMap<String, String>()

    operator fun set(key: String, value: String) {
        mAttrs[key] = value
    }

    fun getString(key: String): String? = getString(key, null)

    fun getString(key: String, defVal: String?): String? = if(mAttrs.containsKey(key)) mAttrs[key] else defVal

    fun getInt(key: String): Int = getInt(key, 0)

    fun getInt(key: String, defVal: Int): Int = if(mAttrs.containsKey(key)) mAttrs[key]?.toInt()!! else defVal
}