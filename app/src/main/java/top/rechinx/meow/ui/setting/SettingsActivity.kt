package top.rechinx.meow.ui.setting

import top.rechinx.meow.R
import top.rechinx.meow.ui.base.BaseActivity

class SettingsActivity: BaseActivity() {


    override fun initToolbar() {
        super.initToolbar()
        toolbar?.title = getString(R.string.title_activity_settings)
    }

    override fun initViews() {
        toolbar?.setNavigationOnClickListener { finish() }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container, MainSettingsFragment()).commit()
    }

    override fun getLayoutId(): Int = R.layout.activity_settings

}