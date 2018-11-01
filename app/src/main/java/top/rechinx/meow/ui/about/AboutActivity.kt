package top.rechinx.meow.ui.about

import android.content.Context
import android.content.Intent
import android.widget.TextView
import org.w3c.dom.Text
import top.rechinx.meow.R
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseActivity

class AboutActivity: BaseActivity() {

    private val updateText by bindView<TextView>(R.id.about_update_summary)
    private val versionName by bindView<TextView>(R.id.about_version_name)

    private var version: String? = null
    override fun initViews() {
        toolbar?.setNavigationOnClickListener { finish() }
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