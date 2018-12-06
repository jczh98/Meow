package top.rechinx.meow.ui.reader

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.widget.NestedScrollView
import android.view.View
import android.widget.AdapterView
import android.widget.CompoundButton
import com.f2prateek.rx.preferences2.Preference
import kotlinx.android.synthetic.main.custom_setting_sheet.*
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.meow.ui.reader.viewer.pager.PagerViewer
import top.rechinx.meow.ui.reader.viewer.webtoon.WebtoonViewer
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible

class ReaderSetting(val activity: ReaderActivity) : BottomSheetDialog(activity){

    private val preferences: PreferenceHelper by activity.inject()

    init {
        val view = activity.layoutInflater.inflate(R.layout.custom_setting_sheet, null)
        val scroll = NestedScrollView(activity)
        scroll.addView(view)
        setContentView(scroll)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGeneralPreferences()

        when (activity.viewer) {
            is PagerViewer -> initPagerPreferences()
            is WebtoonViewer -> initWebtoonPreferences()
        }
    }

    /**
     * Init general reader preferences.
     */
    private fun initGeneralPreferences() {
        viewerSelectionSpinner.setSelection(activity.presenter.manga?.viewer ?: 0, false)
        viewerSelectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                activity.presenter.setMangaViewer(position)
                when (activity.presenter.getMangaViewer()) {
                    1, 2 -> initPagerPreferences()
                    3 -> initWebtoonPreferences()
                }
            }
        }
        hideInfoSwitch.bindToPreference(preferences.hiddenReaderInfo())
        fullscreenSwitch.bindToPreference(preferences.fullscreen())
    }

    /**
     * Init the preferences for the pager reader.
     */
    private fun initPagerPreferences() {
        webtooGroup.gone()
        pagerGroup.visible()
        pageTransitions.bindToPreference(preferences.pageTransitions())
    }

    /**
     * Init the preferences for the webtoon reader.
     */
    private fun initWebtoonPreferences() {
        pagerGroup.gone()
        webtooGroup.visible()
    }

    /**
     * Binds a checkbox or switch view with a boolean preference.
     */
    private fun CompoundButton.bindToPreference(pref: Preference<Boolean>) {
        isChecked = pref.getOrDefault()
        setOnCheckedChangeListener { _, isChecked -> pref.set(isChecked) }
    }

}