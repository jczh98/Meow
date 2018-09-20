package top.rechinx.meow.manager

import android.content.Context
import android.content.SharedPreferences

class PreferenceManager(context: Context) {

    private val mSharedPreferences: SharedPreferences

    init {
        mSharedPreferences = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    }

    @JvmOverloads
    fun getString(key: String, defValue: String? = null): String? {
        return mSharedPreferences.getString(key, defValue)
    }

    fun putString(key: String, value: String) {
        mSharedPreferences.edit().putString(key, value).apply()
    }

    fun getBoolean(key: String, defValue: Boolean): Boolean {
        return mSharedPreferences.getBoolean(key, defValue)
    }

    fun putBoolean(key: String, value: Boolean) {
        mSharedPreferences.edit().putBoolean(key, value).apply()
    }

    fun getInt(key: String, defValue: Int): Int {
        return mSharedPreferences.getInt(key, defValue)
    }

    fun putInt(key: String, value: Int) {
        mSharedPreferences.edit().putInt(key, value).apply()
    }

    fun getLong(key: String, defValue: Long): Long {
        return mSharedPreferences.getLong(key, defValue)
    }

    fun putLong(key: String, value: Long) {
        mSharedPreferences.edit().putLong(key, value).apply()
    }

    companion object {

        private val PREFERENCES_NAME = "meow_preferences"
    }

}