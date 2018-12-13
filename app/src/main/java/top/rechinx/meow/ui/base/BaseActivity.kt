package top.rechinx.meow.ui.base

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.rikka.theme.utils.ThemeUtils
import top.rechinx.meow.R.color.theme_color_primary_dark
import android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS
import android.view.WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS
import android.os.Build.VERSION.SDK_INT
import android.view.WindowManager
import androidx.annotation.Nullable
import top.rechinx.meow.R
import top.rechinx.meow.utils.ThemeHelper
import top.rechinx.rikka.mvp.BaseAppCompatActivity


abstract class BaseActivity: BaseAppCompatActivity() {

    override fun onPostCreate(@Nullable savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        if (Build.VERSION.SDK_INT >= 21) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS)
            window.statusBarColor = ThemeUtils.getColorById(this, R.color.theme_color_primary_dark)
            val description = ActivityManager.TaskDescription(null, null,
                    ThemeUtils.getThemeAttrColor(this, android.R.attr.colorPrimary))
            setTaskDescription(description)
        }
    }

    override fun isNightTheme(): Boolean {
        return ThemeHelper.isNightTheme(this)
    }

}