package top.rechinx.meow.module.setting

import android.os.Bundle
import android.preference.ListPreference
import android.preference.Preference
import android.preference.PreferenceFragment
import android.preference.SwitchPreference
import android.view.View
import android.widget.ListView
import android.widget.Switch
import top.rechinx.meow.Constants
import top.rechinx.meow.R
import top.rechinx.meow.manager.PreferenceManager

class MainSettingsFragment: PreferenceFragment(), Preference.OnPreferenceChangeListener {

    private lateinit var mPreferenceManager: PreferenceManager
    private lateinit var mReaderMode: ListPreference
    private lateinit var mHiddenReaderInfo: SwitchPreference

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
        mPreferenceManager = PreferenceManager(activity)
        initPreference()
        setupPreference()
    }

    private fun setupPreference() {
        mReaderMode.value = mPreferenceManager.getString(Constants.PREF_READER_MODE, "0")
        mReaderMode.summary = mReaderMode.entry
        mReaderMode.onPreferenceChangeListener = this
        mHiddenReaderInfo.isChecked = mPreferenceManager.getBoolean(Constants.PREF_HIDE_READER_INFO, false)
        mHiddenReaderInfo.onPreferenceChangeListener = this
    }

    private fun initPreference() {
        mReaderMode = findPreference(Constants.PREF_READER_MODE) as ListPreference
        mHiddenReaderInfo = findPreference(Constants.PREF_HIDE_READER_INFO) as SwitchPreference
    }

    override fun onPreferenceChange(preference: Preference?, newValue: Any?): Boolean {
        when (preference) {
            mReaderMode -> {
                mReaderMode.value = newValue as String
                mReaderMode.summary = mReaderMode.entry
                mPreferenceManager.putString(mReaderMode.key, newValue)
            }
            mHiddenReaderInfo -> {
                mHiddenReaderInfo.isChecked = newValue as Boolean
                mPreferenceManager.putBoolean(mHiddenReaderInfo.key, newValue)
            }
            else -> {

            }
        }
        return super.onPreferenceTreeClick(preferenceScreen, preference)
    }
}