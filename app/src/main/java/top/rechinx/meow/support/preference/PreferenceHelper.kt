package top.rechinx.meow.support.preference

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import com.f2prateek.rx.preferences2.RxSharedPreferences
import top.rechinx.meow.global.Constants

class PreferenceHelper(context: Context) {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val rxPrefs = RxSharedPreferences.create(prefs)


    fun fullscreen() = rxPrefs.getBoolean(Constants.PREF_FULL_SCREEN, true)

    fun pageTransitions() = rxPrefs.getBoolean(Constants.PREF_ENABLE_TRANSITIONS, true)

    companion object {

        private const val PREFERENCES_NAME = "meow_preferences"
    }

}