package top.rechinx.meow.ui.setting

import android.app.Activity
import android.app.ActivityManager
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.LinearLayout
import androidx.annotation.ColorRes
import androidx.annotation.StringRes
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import kotlinx.android.synthetic.main.activity_theme.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.utils.ThemeHelper
import top.rechinx.rikka.theme.utils.ThemeUtils

class ThemeActivity : BaseActivity(), BaseAdapter.OnItemClickListener {

    private lateinit var adapter : ThemeAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_theme)
        setSupportActionBar(customToolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        customToolbar.setNavigationOnClickListener {
            setResult(Activity.RESULT_OK)
            finish()
        }
        supportActionBar?.title = getString(R.string.title_activity_theme)

        recyclerView.setHasFixedSize(true)
        val list = arrayListOf(
                getTriple(ThemeHelper.THEME_BLUE, R.color.blue, R.string.color_blue),
                getTriple(ThemeHelper.THEME_NIGHT, R.color.black, R.string.color_black),
                getTriple(ThemeHelper.THEME_PINK, R.color.pink, R.string.color_pink),
                getTriple(ThemeHelper.THEME_PURPLE, R.color.purple, R.string.color_purple),
                getTriple(ThemeHelper.THEME_GREEN, R.color.green, R.string.color_green),
                getTriple(ThemeHelper.THEME_GREEN_LIGHT, R.color.green_light, R.string.color_green_light),
                getTriple(ThemeHelper.THEME_YELLOW, R.color.yellow, R.string.color_yellow),
                getTriple(ThemeHelper.THEME_ORANGE, R.color.orange, R.string.color_orange),
                getTriple(ThemeHelper.THEME_RED, R.color.red, R.string.color_red)
        )
        adapter = ThemeAdapter(this, list)
        adapter.setOnItemClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = adapter
        recyclerView.addItemDecoration(DividerItemDecoration(this, LinearLayout.VERTICAL))
    }

    override fun onBackPressed() {
        setResult(Activity.RESULT_OK)
        super.onBackPressed()
    }

    override fun onItemClick(view: View, position: Int) {
        val theme = adapter.getItem(position)
        val currentTheme = ThemeHelper.getTheme(this)
        for (index in adapter.datas.indices) {
            val item = adapter.getItem(index)
            if (item.first == currentTheme) {
                adapter.notifyItemChanged(index)
                break
            }
        }
        adapter.notifyItemChanged(position)
        if (theme.first != currentTheme) {
            switchTheme(theme.first)
        }
    }

    private fun switchTheme(themeId: Int) {
        val isNight = ThemeHelper.isNightTheme(this)
        ThemeHelper.setTheme(this, themeId)
        if (isNight) {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_NO)
        }
        if (themeId == ThemeHelper.THEME_NIGHT) {
            delegate.setLocalNightMode(AppCompatDelegate.MODE_NIGHT_YES)
        } else {
            ThemeUtils.refreshUI(this, object : ThemeUtils.ExtraRefreshable {
                override fun refreshGlobal(activity: Activity) {
                    //for global setting, just do once
                    if (Build.VERSION.SDK_INT >= 21) {
                        val context = activity
                        val taskDescription = ActivityManager.TaskDescription(null, null,
                                ThemeUtils.getThemeAttrColor(context, android.R.attr.colorPrimary))
                        setTaskDescription(taskDescription)
                        window.statusBarColor = ThemeUtils.getColorById(context, R.color.theme_color_primary_dark)
                    }
                }

                override fun refreshSpecificView(view: View) {
                    //TODO: will do this for each traversal
                }
            }
            )
        }
    }

    private fun getTriple(themeId: Int, @ColorRes colorRes: Int, @StringRes stringRes: Int) : Triple<Int, Int, String> {
        return Triple(themeId, ContextCompat.getColor(this, colorRes), getString(stringRes))
    }
}
