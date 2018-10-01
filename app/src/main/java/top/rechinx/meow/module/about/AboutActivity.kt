package top.rechinx.meow.module.about

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import com.allenliu.versionchecklib.v2.AllenVersionChecker
import com.allenliu.versionchecklib.v2.builder.RequestVersionBuilder
import com.allenliu.versionchecklib.v2.builder.UIData
import com.allenliu.versionchecklib.v2.callback.RequestVersionListener
import me.drakeet.multitype.Items
import me.drakeet.support.about.AbsAboutActivity
import me.drakeet.support.about.Card
import me.drakeet.support.about.Category
import me.drakeet.support.about.Contributor
import top.rechinx.meow.BuildConfig
import top.rechinx.meow.R
import me.drakeet.support.about.License
import org.json.JSONObject
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.support.log.L


class AboutActivity: BaseActivity() {

    @BindView(R.id.about_update_summary) lateinit var mUpdateText: TextView
    @BindView(R.id.about_version_name) lateinit var mVersionName: TextView
    @BindView(R.id.about_layout) lateinit var mLayoutView: View

    private lateinit var mVersion: String

    override fun initData() {
    }

    override fun initView() {
        mToolbar?.setNavigationOnClickListener { finish() }
        supportActionBar?.title = getString(R.string.drawer_about)
        try {
            val info = packageManager.getPackageInfo(packageName, 0)
            mVersion = info.versionName
            mVersionName.text = "Version: $mVersion"
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    @OnClick(R.id.about_resource_btn) fun onResourceClick() {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.about_resource_url)))
        try {
            startActivity(intent)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @OnClick(R.id.about_update_btn) fun onUpdateClick() {
        mUpdateText.text = getString(R.string.request_version_checking)
        AllenVersionChecker.getInstance()
                .requestVersion()
                .setRequestUrl("https://api.github.com/repos/ReChinX/Meow/releases/latest")
                .request(object :RequestVersionBuilder(), RequestVersionListener {
                    override fun onRequestVersionSuccess(result: String?): UIData? {
                        val json = JSONObject(result)
                        if(json.getString("tag_name") == mVersion) {
                            mUpdateText.text = getString(R.string.request_version_latest)
                            return null
                        }
                        mUpdateText.text = getString(R.string.request_version_latest)
                        return UIData.create()
                                .setTitle(getString(R.string.request_version_title))
                                .setContent(getString(R.string.request_version_content))
                                .setDownloadUrl(json.getJSONArray("assets").getJSONObject(0).getString("browser_download_url"))
                    }

                    override fun onRequestVersionFailure(message: String?) {
                        mUpdateText.text = getString(R.string.request_version_error)
                    }

                }).excuteMission(this)
    }

    override fun getLayoutId(): Int = R.layout.activity_about

    override fun initPresenter() {
    }


    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }
}