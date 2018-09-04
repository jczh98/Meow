package top.rechinx.meow.module.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.design.widget.TabLayout
import android.support.v4.view.GravityCompat
import android.support.v4.view.ViewPager
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import butterknife.BindView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.R
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.favorite.FavoriteFragment
import top.rechinx.meow.module.result.ResultActivity
import top.rechinx.meow.module.search.SearchFragment
import top.rechinx.meow.source.Dmzj
import top.rechinx.meow.source.Shuhui

class HomeActivity : BaseActivity(), NavigationView.OnNavigationItemSelectedListener {

    @BindView(R.id.drawer_layout) lateinit var mDrawerLayout: DrawerLayout
    @BindView(R.id.navigation_view) lateinit var mNavigationView: NavigationView
    @BindView(R.id.search_view) lateinit var mSearchView: MaterialSearchView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer()
        supportFragmentManager.beginTransaction().replace(R.id.container_fragment, HomeFragment()).commit()
    }

    private fun initDrawer() {
        val drawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0)
        drawerToggle.syncState()
        mDrawerLayout.setDrawerListener(drawerToggle)
        mNavigationView.setNavigationItemSelectedListener(this)
    }

    override fun initView() {
        mSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    mSearchView.setHint(getString(R.string.empty_for_search))
                } else {
                    startActivity(ResultActivity.createIntent(this@HomeActivity, query, intArrayOf(Dmzj.TYPE, Shuhui.TYPE)))
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }

    override fun initPresenter() {

    }

    override fun initData() {
        SourceManager.getInstance().initSource()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    override fun getLayoutId(): Int = R.layout.activity_home

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.bar_menu, menu);
        val item = menu?.findItem(R.id.action_search)
        mSearchView.setMenuItem(item)
        return true
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.drawer_main -> {
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, HomeFragment()).commit()
            }
            R.id.drawer_favorite -> {
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, FavoriteFragment()).commit()
            }
        }
        mDrawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    override fun onBackPressed() {
        if (mSearchView.isSearchOpen) {
            mSearchView.closeSearch()
        } else {
            super.onBackPressed()
        }
    }
}
