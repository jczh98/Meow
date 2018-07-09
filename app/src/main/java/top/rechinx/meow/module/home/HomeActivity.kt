package top.rechinx.meow.module.home

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.NavigationView
import android.support.v4.widget.DrawerLayout
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.module.base.BaseActivity

class HomeActivity : BaseActivity() {

    @BindView(R.id.drawer_layout)
    lateinit var mDrawerLayout: DrawerLayout

    @BindView(R.id.navigation_view)
    lateinit var mNavigationView: NavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initDrawer()
    }

    private fun initDrawer() {
        val drawerToggle = ActionBarDrawerToggle(this, mDrawerLayout, mToolbar, 0, 0)
        drawerToggle.syncState()
        mDrawerLayout.setDrawerListener(drawerToggle)
    }


    override fun getLayoutId(): Int = R.layout.activity_home
}
