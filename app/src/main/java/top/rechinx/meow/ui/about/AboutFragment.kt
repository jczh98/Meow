package top.rechinx.meow.ui.about

import android.app.Activity
import android.app.ActivityManager
import android.content.Intent
import android.content.res.Configuration
import android.os.Build
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_about.*
import top.rechinx.meow.R
import top.rechinx.meow.ui.extension.ExtensionActivity
import top.rechinx.meow.ui.setting.SettingsActivity
import top.rechinx.rikka.theme.utils.ThemeUtils
import top.rechinx.meow.ui.home.MainActivity
import android.os.Build.VERSION.SDK_INT
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import timber.log.Timber
import top.rechinx.meow.ui.setting.ThemeActivity
import top.rechinx.meow.utils.ThemeHelper



class AboutFragment: Fragment() {

    private var version: String? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        setHasOptionsMenu(true)
        return inflater.inflate(R.layout.fragment_about, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val info = activity!!.packageManager.getPackageInfo(activity!!.packageName, 0)
            version = info.versionName
            versionName.text = "Version: $version"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        menu.clear()
        inflater.inflate(R.menu.menu_about, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.action_settings -> {
                val intent = Intent(activity, SettingsActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_extension -> {
                val intent = Intent(activity, ExtensionActivity::class.java)
                startActivity(intent)
                true
            }
            R.id.action_theme -> {
                startActivityForResult(Intent(activity, ThemeActivity::class.java), REFRESH_THEME)
                true
            }
            else -> {
                super.onOptionsItemSelected(item)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REFRESH_THEME -> {
                    (activity as MainActivity).refreshTheme()
                }
            }
        }
    }

    companion object {

        const val REFRESH_THEME = 777

    }
}