package top.rechinx.meow.ui.base

import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.annotation.Nullable
import top.rechinx.meow.R
import top.rechinx.meow.utils.ThemeHelper
import top.rechinx.rikka.mvp.MvpAppCompatActivityWithoutReflection
import top.rechinx.rikka.mvp.presenter.Presenter
import top.rechinx.rikka.theme.utils.ThemeUtils

abstract class BaseMvpActivityWithoutReflection<P : Presenter<*>> : MvpAppCompatActivityWithoutReflection<P>() {

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