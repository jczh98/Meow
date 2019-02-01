package top.rechinx.meow.data.preference

import android.content.Context
import android.net.Uri
import android.os.Environment
import com.f2prateek.rx.preferences2.Preference
import com.f2prateek.rx.preferences2.RxSharedPreferences
import top.rechinx.meow.global.Constants
import java.io.File

fun <T> Preference<T>.getOrDefault(): T = get() ?: defaultValue()!!

class PreferenceHelper(context: Context) {

    private val prefs = context.getSharedPreferences(PREFERENCES_NAME, Context.MODE_PRIVATE)
    private val rxPrefs = RxSharedPreferences.create(prefs)

    private val defaultDownloadsDir = Uri.fromFile(
            File(Environment.getExternalStorageDirectory().absolutePath + File.separator +
                    "Meow", "downloads"))

    fun fullscreen() = rxPrefs.getBoolean(Constants.PREF_FULL_SCREEN, true)

    fun pageTransitions() = rxPrefs.getBoolean(Constants.PREF_ENABLE_TRANSITIONS, true)

    fun readerMode() = prefs.getInt(Constants.PREF_READER_MODE, 1)

    fun hiddenReaderInfo() = rxPrefs.getBoolean(Constants.PREF_HIDE_READER_INFO, false)

    fun sourceSwitch(sourceId: Long) = rxPrefs.getBoolean("source_switch_$sourceId", false)

    fun setSourceSwitch(sourceId: Long, switcher: Boolean) {
        prefs.edit().putBoolean("source_switch_$sourceId", switcher)
                .apply()
    }

    fun downloadOnlyOverWifi() = prefs.getBoolean(Constants.PREF_DOWNLOAD_ONLY_WIFI, true)

    fun downloadsDirectory() = rxPrefs.getString(Constants.PREF_DOWNLOAD_DIRECTORY, defaultDownloadsDir.toString())

    fun enableVolumeKeys() = rxPrefs.getBoolean(Constants.PREF_ENABLE_VOLUME_KEYS, false)

    fun readWithVolumeKeysInverted() = rxPrefs.getBoolean(Constants.PREF_READ_WITH_VOLUME_KEYS_INVERTED, false)

    companion object {

        const val PREFERENCES_NAME = "meow_preferences"
    }

}