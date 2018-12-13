package top.rechinx.meow.utils

import android.content.Context
import android.content.SharedPreferences


object ThemeHelper {

    private val CURRENT_THEME = "theme_current"

    const val THEME_BLUE = 0x1
    const val THEME_PINK = 0x2
    const val THEME_PURPLE = 0x3
    const val THEME_GREEN = 0x4
    const val THEME_GREEN_LIGHT = 0x5
    const val THEME_YELLOW = 0x6
    const val THEME_ORANGE = 0x7
    const val THEME_RED = 0x8
    const val THEME_NIGHT = 0x9

    fun getSharePreference(context: Context): SharedPreferences {
        return context.getSharedPreferences("multiple_theme", Context.MODE_PRIVATE)
    }

    fun setTheme(context: Context, themeId: Int) {
        getSharePreference(context).edit()
                .putInt(CURRENT_THEME, themeId)
                .apply()
    }

    fun getTheme(context: Context): Int {
        return getSharePreference(context).getInt(CURRENT_THEME, THEME_BLUE)
    }

    fun isDefaultTheme(context: Context): Boolean {
        return getTheme(context) == THEME_BLUE
    }

    fun isNightTheme(context: Context) : Boolean {
        return getTheme(context) == THEME_NIGHT
    }

}