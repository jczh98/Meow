package top.rechinx.meow.ui.about

import android.content.Context
import android.content.Intent
import kotlinx.android.synthetic.main.activity_about.*
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.ui.base.BaseActivity

class AboutActivity: BaseActivity() {

    private var version: String? = null
    override fun initViews() {
        customToolbar?.setNavigationOnClickListener { finish() }
        supportActionBar?.title = getString(R.string.drawer_about)
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            version = info.versionName
            versionName.text = "Version: $version"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_about

    companion object {
        fun createIntent(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }
}