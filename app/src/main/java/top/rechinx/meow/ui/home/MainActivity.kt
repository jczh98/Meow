package top.rechinx.meow.ui.home

import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import com.miguelcatalan.materialsearchview.MaterialSearchView
import top.rechinx.meow.R
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.result.ResultActivity

class MainActivity: BaseActivity(), NavigationView.OnNavigationItemSelectedListener, MaterialSearchView.OnQueryTextListener {

    private val drawerLayout  by bindView<DrawerLayout>(R.id.drawer_layout)
    private val navigationView by bindView<NavigationView>(R.id.navigation_view)
    private val searchView by bindView<MaterialSearchView>(R.id.search_view)

    override fun initViews() {
        initDrawer()
        searchView.setOnQueryTextListener(this)
    }

    private fun initDrawer() {
        val drawerToggle = ActionBarDrawerToggle(this, drawerLayout, toolbar, 0, 0)
        drawerToggle.syncState()
        drawerLayout.addDrawerListener(drawerToggle)
        navigationView.setNavigationItemSelectedListener(this)
    }

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {

        }
        drawerLayout.closeDrawer(GravityCompat.START)
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