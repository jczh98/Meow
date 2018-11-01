package top.rechinx.meow.ui.setting

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.View
import android.widget.ListView
import org.koin.android.ext.android.inject
import top.rechinx.meow.global.Constants
import top.rechinx.meow.R
import top.rechinx.meow.support.preference.PreferenceHelper

class MainSettingsFragment: PreferenceFragment(), Preference.OnPreferenceChangeListener {

    private val preferences: PreferenceHelper by inject()
    private lateinit var readerMode: ListPreference
    private lateinit var hiddenReaderInfo: SwitchPreference

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        var list = view.findViewById<ListView>(android.R.id.list)
        list.divider = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        addPreferencesFromResource(R.xml.pref_settings)
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initPreference()
        setupPreference()
    }

    private fun setupPreference() {
        readerMode.value = preferences.readerMode().asObservable().blockingFirst()
        readerMode.summary = readerMode.entry
        readerMode.onPreferenceChangeListener = this
        hiddenReaderInfo.isChecked = preferences.hiddenReaderInfo().asObservable().blockingFirst()
        hiddenReaderInfo.onPreferenceChangeListener = this
    }

    private fun initPreference() {
        readerMode = findPreference(Constants.PREF_READER_MODE) as ListPreference
        hiddenReaderInfo = findPreference(Constants.PREF_HIDE_READER_INFO) as SwitchPreference
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference) {
            readerMode -> {
                readerMode.value = newValue as String
                readerMode.summary = readerMode.entry
            }
            hiddenReaderInfo -> {
                hiddenReaderInfo.isChecked = newValue as Boolean
            }
            else -> {

            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }
}