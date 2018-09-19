package top.rechinx.meow.module.home

import android.Manifest
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.view.GravityCompat
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.Menu
import android.view.MenuItem
import butterknife.BindView
import com.miguelcatalan.materialsearchview.MaterialSearchView
import com.tbruyelle.rxpermissions2.RxPermissions
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import org.reactivestreams.Publisher
import top.rechinx.meow.R
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.about.AboutActivity
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.favorite.HistoryFragment
import top.rechinx.meow.module.result.ResultActivity
import top.rechinx.meow.module.source.SourceFragment
import top.rechinx.meow.source.Dmzj
import top.rechinx.meow.source.Kuaikan
import top.rechinx.meow.source.Shuhui
import top.rechinx.meow.support.relog.ReLog
import top.rechinx.meow.App
import top.rechinx.meow.model.Comic
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.nio.charset.Charset


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
        RxPermissions(this).request(Manifest.permission.READ_EXTERNAL_STORAGE).subscribe()
        test()
        mNavigationView.setCheckedItem(R.id.drawer_main)
        mSearchView.setOnQueryTextListener(object : MaterialSearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String): Boolean {
                if (query.isEmpty()) {
                    mSearchView.setHint(getString(R.string.empty_for_search))
                } else {
                    startActivity(ResultActivity.createIntent(this@HomeActivity, query))
                }
                return false
            }

            override fun onQueryTextChange(newText: String): Boolean {
                return false
            }
        })
    }


    fun test() {
        ReLog.d(SourceManager.getInstance().getSourceNames()[0])
//        var iss = assets.open("dmzj.xml");
//        var lenght = iss.available()
//        var  buffer = ByteArray(lenght)
//        iss.read(buffer)
//        var source = String(buffer, Charset.forName("utf8"))
//        var s = SaSource(App.instance, source)
//        var comic = Comic()
//        comic.cid = "21"
//        CompositeDisposable().add(s.getSearchResult("akb", 0)
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    ReLog.d(it.title)
//                }, {ReLog.d(it.message)}))

    }
    override fun initPresenter() {

    }

    override fun initData() {
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
                mToolbar?.title = getString(R.string.app_name)
            }
            R.id.drawer_source -> {
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, SourceFragment()).commit()
                mToolbar?.title = item.title
            }
            R.id.drawer_history -> {
                supportFragmentManager.beginTransaction().replace(R.id.container_fragment, HistoryFragment()).commit()
                mToolbar?.title = item.title
            }
            R.id.drawer_about -> {
                startActivity(AboutActivity.createIntent(this))
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
