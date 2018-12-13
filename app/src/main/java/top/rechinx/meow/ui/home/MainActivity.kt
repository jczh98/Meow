package top.rechinx.meow.ui.home

import android.Manifest
import android.app.Activity
import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.ColorStateList
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.ContextCompat.startActivity
import androidx.fragment.app.Fragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.functions.Action
import io.reactivex.functions.Consumer
import io.reactivex.rxkotlin.plusAssign
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.global.Extras
import top.rechinx.meow.ui.about.AboutFragment
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.meow.ui.result.ResultActivity
import top.rechinx.meow.ui.source.SourceFragment
import top.rechinx.rikka.fragment.FragmentNavigator
import top.rechinx.rikka.fragment.FragmentNavigatorAdapter
import top.rechinx.rikka.rxbus.RxBus
import top.rechinx.rikka.theme.utils.ThemeUtils


class MainActivity: BaseActivity(), MaterialSearchView.OnQueryTextListener, BottomNavigationView.OnNavigationItemSelectedListener {

    // Instance all fragments
    private val fragments = arrayOf(HomeFragment(), SourceFragment(), AboutFragment())
    private val ids = arrayOf(R.id.drawer_main, R.id.drawer_source, R.id.drawer_about)
    private var latestedId = R.id.drawer_main

    private lateinit var navigator : FragmentNavigator

    private val disposables = CompositeDisposable()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_home)
        initToolbar()
        initViews(savedInstanceState)
    }

    override fun onDestroy() {
        disposables.dispose()
        super.onDestroy()
    }

    private fun setCurrentTab(position: Int) {
        navigator.showFragment(position)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        navigator.onSaveInstanceState(outState)
    }

    private fun initViews(savedInstanceState: Bundle?) {
        bottomNavigation.itemIconTintList = ThemeUtils.getThemeColorStateList(this, R.color.bottom_navigation_colors)
        bottomNavigation.itemTextColor = ThemeUtils.getThemeColorStateList(this, R.color.bottom_navigation_colors)
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        searchView.setOnQueryTextListener(this)
        requestStoragePermission(0)

        // fragments
        navigator = FragmentNavigator(supportFragmentManager, FragmentAdapter(), R.id.containerFragment)
        navigator.setDefaultPosition(0)
        navigator.onCreate(savedInstanceState)

    }

    fun refreshTheme() {
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
    override fun onStart() {
        super.onStart()
        bottomNavigation.selectedItemId = latestedId
    }

    private fun initToolbar() {
        setSupportActionBar(customToolbar)
        supportActionBar?.setHomeButtonEnabled(false)
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }

    fun requestStoragePermission(requestCode: Int) {
        if(checkPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE, requestCode)) {

        } else {
            requestPermission(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), requestCode)
        }
    }

    fun checkPermission(context: Context, permission: String, code: Int): Boolean {
        return ContextCompat.checkSelfPermission(context, permission) == PackageManager.PERMISSION_GRANTED
    }

    fun requestPermission(context: Context, permissions: Array<String>, code: Int) {
        ActivityCompat.requestPermissions(this, permissions, code)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.drawer_main -> {
                setCurrentTab(0)
                customToolbar?.title = getString(R.string.app_name)
            }
            R.id.drawer_source -> {
                setCurrentTab(1)
                customToolbar?.title = item.title
            }
            R.id.drawer_about -> {
                setCurrentTab(2)
                customToolbar?.title = item.title
            }
        }
        latestedId = item.itemId
        return true
    }


    override fun onQueryTextSubmit(query: String): Boolean {
        if (query.isEmpty()) {
            searchView.setHint(getString(R.string.empty_for_search))
        } else {
            startActivity(ResultActivity.createIntent(this@MainActivity, query))
        }
        return false
    }

    override fun onQueryTextChange(newText: String?): Boolean {
        return false
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bar_menu, menu);
        val item = menu.findItem(R.id.action_search)
        searchView.setMenuItem(item)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }

    inner class FragmentAdapter : FragmentNavigatorAdapter {

        private val TAGS = arrayOf("HOME", "SOURCE", "ABOUT")

        override val count: Int = TAGS.size

        override fun onCreateFragment(position: Int): Fragment {
            return fragments[position]
        }

        override fun getTag(position: Int): String {
            return TAGS[position]
        }

    }
}