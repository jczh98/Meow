package top.rechinx.meow.ui.setting

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.util.TypedValue
import android.view.ContextThemeWrapper
import androidx.preference.PreferenceFragmentCompat
import com.afollestad.materialdialogs.MaterialDialog
import com.afollestad.materialdialogs.files.folderChooser
import com.hippo.unifile.UniFile
import org.koin.android.ext.android.inject
import top.rechinx.meow.global.Constants
import top.rechinx.meow.R
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.rikka.preference.*

class MainSettingsFragment: PreferenceFragmentCompat() {

    val preferences by inject<PreferenceHelper>()

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        // Set preferences without dividers
        setDivider(ColorDrawable(Color.TRANSPARENT))
        setDividerHeight(0)
    }

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        preferenceManager.sharedPreferencesName = PreferenceHelper.PREFERENCES_NAME
        val screen = preferenceManager.createPreferenceScreen(getThemedContext())
        preferenceScreen = screen
        preferenceScreen.apply {
            preferenceCategory {
                titleRes = R.string.pref_reader_settings

                intListPreference {
                    key = Constants.PREF_READER_MODE
                    titleRes = R.string.pref_default_reader_mode
                    entriesRes = arrayOf(R.string.left_to_right_viewer,
                            R.string.right_to_left_viewer,
                            R.string.webtoon_viewer)
                    entryValues = arrayOf("1", "2", "3")
                    defaultValue = "1"
                    summary = "%s"
                }
                switchPreference {
                    key = Constants.PREF_FULL_SCREEN
                    titleRes = R.string.pref_full_screen
                    defaultValue = true
                }

                switchPreference {
                    key = Constants.PREF_HIDE_READER_INFO
                    titleRes = R.string.pref_hide_reader_status
                    defaultValue = false
                }

                switchPreference {
                    key = Constants.PREF_ENABLE_VOLUME_KEYS
                    titleRes = R.string.pref_allow_volume_keys
                    defaultValue = false
                }
            }

            preferenceCategory {
                titleRes = R.string.pref_reader_mode_page

                switchPreference {
                    key = Constants.PREF_ENABLE_TRANSITIONS
                    titleRes = R.string.pref_page_transitions
                    defaultValue = true
                }
            }

            preferenceCategory {
                titleRes = R.string.pref_download

                preference {
                    titleRes = R.string.pref_download_directory
                    onClick {
                        MaterialDialog(context).show {
                            folderChooser { dialog, file ->
                                preferences.downloadsDirectory().set(Uri.fromFile(file).toString())
                            }
                        }
                    }

                    preferences.downloadsDirectory().asObservable()
                            .subscribe {
                                val dir = UniFile.fromUri(context, Uri.parse(it))
                                summary = dir.filePath ?: it
                            }
                }
            }
        }
    }

    private fun getThemedContext(): Context {
        val tv = TypedValue()
        activity!!.theme.resolveAttribute(R.attr.preferenceTheme, tv, true)
        return ContextThemeWrapper(activity, tv.resourceId)
    }

}