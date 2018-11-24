package top.rechinx.meow.ui.home

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.view.Menu
import android.view.MenuItem
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.ui.about.AboutFragment
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.result.ResultActivity
import top.rechinx.meow.ui.source.SourceFragment

class MainActivity: BaseActivity(), MaterialSearchView.OnQueryTextListener, BottomNavigationView.OnNavigationItemSelectedListener {

    // Instance all fragments
    private var homeFragment = HomeFragment()
    private var sourceFragment = SourceFragment()
    private var aboutFragment = AboutFragment()

    override fun initViews() {
        bottomNavigation.setOnNavigationItemSelectedListener(this)
        searchView.setOnQueryTextListener(this)
        requestStoragePermission(0)
        // Init fragments
        supportFragmentManager.beginTransaction()
                .add(R.id.containerFragment, homeFragment)
                .add(R.id.containerFragment, sourceFragment)
                .add(R.id.containerFragment, aboutFragment)
                .show(homeFragment)
                .hide(sourceFragment)
                .hide(aboutFragment)
                .commit()
        bottomNavigation.selectedItemId = R.id.drawer_main

    }

    override fun initToolbar() {
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


    override fun getLayoutId(): Int = R.layout.activity_home

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.drawer_main -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(sourceFragment)
                        .hide(aboutFragment)
                        .show(homeFragment)
                        .commit()
                customToolbar?.title = getString(R.string.app_name)
            }
            R.id.drawer_source -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(homeFragment)
                        .hide(aboutFragment)
                        .show(sourceFragment)
                        .commit()
                customToolbar?.title = item.title
            }
            R.id.drawer_about -> {
                supportFragmentManager.beginTransaction()
                        .setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out)
                        .hide(homeFragment)
                        .hide(sourceFragment)
                        .show(aboutFragment)
                        .commit()
                customToolbar?.title = item.title
            }
        }
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


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bar_menu, menu);
        val item = menu?.findItem(R.id.action_search)
        searchView.setMenuItem(item)
        return true
    }

    override fun onBackPressed() {
        if (searchView.isSearchOpen) {
            searchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }
}