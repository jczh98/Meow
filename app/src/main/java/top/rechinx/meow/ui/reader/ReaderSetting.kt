package top.rechinx.meow.ui.reader

import android.os.Bundle
import android.support.design.widget.BottomSheetDialog
import android.support.v4.widget.NestedScrollView
import android.support.v7.widget.AppCompatSpinner
import android.view.View
import android.widget.AdapterView
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.preference.PreferenceHelper
import top.rechinx.meow.support.viewbinding.bindView

class ReaderSetting(val activity: ReaderActivity) : BottomSheetDialog(activity){

    private val preferences: PreferenceHelper by activity.inject()

    val viewerSelection by bindView<AppCompatSpinner>(R.id.viewer_selection_spinner)

    init {
        val view = activity.layoutInflater.inflate(R.layout.custom_setting_sheet, null)
        val scroll = NestedScrollView(activity)
        scroll.addView(view)
        setContentView(scroll)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        initGeneralPreferences()
    }

    private fun initGeneralPreferences() {
        viewerSelection.setSelection(activity.presenter.getMangaViewer(), false)
        viewerSelection.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                activity.presenter.setMangaViewer(position)
            }

        }
    }
}