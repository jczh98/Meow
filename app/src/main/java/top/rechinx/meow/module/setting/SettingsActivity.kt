package top.rechinx.meow.module.setting

import kotlinx.android.synthetic.main.activity_settings.view.*
import top.rechinx.meow.R
import top.rechinx.meow.module.base.BaseActivity

class SettingsActivity: BaseActivity() {

    override fun initData() {

    }

    override fun initToolbar() {
        super.initToolbar()
        mToolbar?.title = getString(R.string.title_activity_settings)
    }

    override fun initView() {
        mToolbar?.setNavigationOnClickListener { finish() }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, MainSettingsFragment()).commit()
    }

    override fun getLayoutId(): Int = R.layout.activity_settings

    override fun initPresenter() {
    }

}