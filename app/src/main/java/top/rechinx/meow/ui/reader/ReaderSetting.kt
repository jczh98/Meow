package top.rechinx.meow.ui.reader

import android.os.Bundle
import com.google.android.material.bottomsheet.BottomSheetDialog
import androidx.core.widget.NestedScrollView
import android.view.View
import android.widget.AdapterView
import kotlinx.android.synthetic.main.custom_setting_sheet.*
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.data.preference.PreferenceHelper

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
    }

    private fun initGeneralPreferences() {
        viewerSelectionSpinner.setSelection(activity.presenter.getMangaViewer(), false)
        viewerSelectionSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {}

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                activity.presenter.setMangaViewer(position)
            }

        }
    }
}