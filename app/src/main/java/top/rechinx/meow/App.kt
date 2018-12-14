package top.rechinx.meow

import android.app.Application
import android.content.Context
import android.graphics.Color
import androidx.annotation.ColorInt
import androidx.annotation.ColorRes
import androidx.appcompat.app.AppCompatDelegate
import com.facebook.stetho.Stetho
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import top.rechinx.meow.di.AppComponent
import top.rechinx.rikka.theme.utils.ThemeUtils
import top.rechinx.meow.utils.ThemeHelper

class App: Application(), ThemeUtils.switchColor {

    private var basePath: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)

        // for Theme
        ThemeUtils.setSwitchColor(this)
        val isNight = ThemeHelper.isNightTheme(this)
        AppCompatDelegate.setDefaultNightMode(
                if (!isNight) AppCompatDelegate.MODE_NIGHT_NO else AppCompatDelegate.MODE_NIGHT_YES)

        // For timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
        // for koin
        startKoin(this, AppComponent.modules())
    }

    fun getBasePath(): String {
        if (basePath == null) {
            basePath = getExternalFilesDir(null).absolutePath
            //basePath = Environment.getExternalStorageDirectory().absolutePath + "/Meow/"
        }
        return basePath!!
    }

    override fun replaceColorById(context: Context, @ColorRes colorId: Int): Int {
        var colorId = colorId
        if (ThemeHelper.isDefaultTheme(context) || ThemeHelper.isNightTheme(context)) {
            return context.resources.getColor(colorId)
        }
        val theme = getTheme(context)
        if (theme != null) {
            colorId = getThemeColorId(context, colorId, theme)
        }
        return context.resources.getColor(colorId)
    }

    override fun replaceColor(context: Context, @ColorInt originColor: Int): Int {
        if (ThemeHelper.isDefaultTheme(context) || ThemeHelper.isNightTheme(context)) {
            return originColor
        }
        val theme = getTheme(context)
        var colorId = -1

        if (theme != null) {
            colorId = getThemeColor(context, originColor, theme)
        }
        return if (colorId != -1) resources.getColor(colorId) else originColor
    }

    private fun getTheme(context: Context): String? {
        return when {
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_PURPLE -> "purple"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_PINK -> "pink"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_GREEN -> "green"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_GREEN_LIGHT -> "green_light"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_YELLOW -> "yellow"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_ORANGE -> "orange"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_RED -> "red"
            ThemeHelper.getTheme(context) == ThemeHelper.THEME_NIGHT -> "night"
            else -> null
        }
    }

    @ColorRes
    private fun getThemeColorId(context: Context, colorId: Int, theme: String): Int {
        when (colorId) {
            R.color.theme_color_primary -> return context.resources.getIdentifier(theme, "color", packageName)
            R.color.theme_color_primary_dark -> return context.resources.getIdentifier(theme + "_dark", "color", packageName)
            R.color.theme_color_primary_trans -> return context.resources.getIdentifier(theme + "_trans", "color", packageName)
        }
        return colorId
    }

    @ColorRes
    private fun getThemeColor(context: Context, color: Int, theme: String): Int {
        when (color) {
            Color.parseColor("#3F51B5") -> return context.resources.getIdentifier(theme, "color", packageName)
            Color.parseColor("#303F9F") -> return context.resources.getIdentifier(theme + "_dark", "color", packageName)
            Color.parseColor("#995d7cf9") -> return context.resources.getIdentifier(theme + "_trans", "color", packageName)
        }
        return -1
    }

    companion object {

        lateinit var instance: App

    }
}