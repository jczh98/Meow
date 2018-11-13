package top.rechinx.meow.ui.setting

import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.ui.base.BaseActivity

class SettingsActivity: BaseActivity() {


    override fun initToolbar() {
        super.initToolbar()
        customToolbar?.title = getString(R.string.title_activity_settings)
    }

    override fun initViews() {
        customToolbar?.setNavigationOnClickListener { finish() }
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameContainer, MainSettingsFragment()).commit()
    }

    override fun getLayoutId(): Int = R.layout.activity_settings

}