package top.rechinx.meow.ui.about

import android.content.Intent
import androidx.lifecycle.ViewModelProviders
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import com.jaredrummler.cyanea.Cyanea
import com.jaredrummler.cyanea.prefs.CyaneaSettingsActivity
import com.jaredrummler.cyanea.utils.ColorUtils
import kotlinx.android.synthetic.main.fragment_about.*
import top.rechinx.meow.R


class AboutFragment : Fragment() {

    private var version: String? = null

    private val cyanea by lazy { Cyanea.instance }

    companion object {
        fun newInstance() = AboutFragment()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up toolbar
        about_toolbar.title = "About"
        about_toolbar.inflateMenu(R.menu.about_menu)
        about_toolbar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_palette -> startActivity(Intent(activity, CyaneaSettingsActivity::class.java))
            }
            true
        }
        // Version info
        try {
            val info = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
            version = info.versionName
            version_name.text = "Version: $version"
        } catch (e: Exception) {
            e.printStackTrace()
        }
        // Tint color
        val textColor = if (ColorUtils.isLightColor(cyanea.primary, 1.0)) {
            ContextCompat.getColor(view.context, R.color.textColorPrimary)
        } else {
            ContextCompat.getColor(view.context, R.color.textColorPrimaryInverse)
        }
        app_name.setTextColor(textColor)
        version_name.setTextColor(textColor)
    }

}
