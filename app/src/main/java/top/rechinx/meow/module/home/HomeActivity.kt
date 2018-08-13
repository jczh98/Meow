package top.rechinx.meow.module.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import butterknife.BindView
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.R
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.search.SearchFragment

class HomeActivity : BaseActivity() {

    @BindView(R.id.drawer_layout)
    lateinit var mDrawerLayout: DrawerLayout

    @BindView(R.id.navigation_view)
    lateinit var mNavigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer()
        supportFragmentManager.beginTransaction().replace(R.id.container_fragment, SearchFragment()).commit()
    }

    private fun initDrawer() {
        val drawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0)
        drawerToggle.syncState()
        mDrawerLayout.setDrawerListener(drawerToggle)
    }

    override fun initView() {

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
}
