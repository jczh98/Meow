package top.rechinx.meow.module.about

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.widget.ImageView
import android.widget.TextView
import me.drakeet.multitype.Items
import me.drakeet.support.about.AbsAboutActivity
import me.drakeet.support.about.Card
import me.drakeet.support.about.Category
import me.drakeet.support.about.Contributor
import top.rechinx.meow.BuildConfig
import top.rechinx.meow.R
import me.drakeet.support.about.License



class AboutActivity: AbsAboutActivity() {

    override fun onItemsCreated(items: Items) {
        items.add(Card("检查更新"))
        items.add(Category("Developers"))
        items.add(Contributor(R.mipmap.ic_launcher, "ReChinX", "Developer"))
        items.add(Category("Open Source Licenses"))
        items.add(License("about-page", "drakeet", License.APACHE_2, "https://github.com/drakeet/about-page"))
    }

    override fun onCreateHeader(icon: ImageView, slogan: TextView, version: TextView) {
        icon.setImageResource(R.mipmap.ic_launcher_round)
        slogan.text = "Meow"
        version.text = "v${BuildConfig.VERSION_CODE}"
    }

    companion object {

        fun createIntent(context: Context): Intent {
            return Intent(context, AboutActivity::class.java)
        }
    }
}