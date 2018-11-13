package top.rechinx.meow.data.preference

import android.content.Context
import com.f2prateek.rx.preferences2.RxSharedPreferences
import top.rechinx.meow.global.Constants

class PreferenceHelper(context: Context) {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val rxPrefs = RxSharedPreferences.create(prefs)


    fun fullscreen() = rxPrefs.getBoolean(Constants.PREF_FULL_SCREEN, true)

    fun pageTransitions() = rxPrefs.getBoolean(Constants.PREF_ENABLE_TRANSITIONS, true)

    fun readerMode() = rxPrefs.getString(Constants.PREF_READER_MODE, "0")

    fun hiddenReaderInfo() = rxPrefs.getBoolean(Constants.PREF_HIDE_READER_INFO, false)
    companion object {

        private const val PREFERENCES_NAME = "meow_preferences"
    }

}