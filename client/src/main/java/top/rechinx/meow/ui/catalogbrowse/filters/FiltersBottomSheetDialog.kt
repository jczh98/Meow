package top.rechinx.meow.ui.catalogbrowse.filters

import android.content.Context
import android.view.View
import android.widget.FrameLayout
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog

/**
 * A custom bottom sheet dialog required to retrieve the [BottomSheetBehavior] and delegate the
 * slide calls to our [FiltersBottomSheet].
 */
class FiltersBottomSheetDialog(context: Context) : BottomSheetDialog(context) {

    /**
     * The default bottom sheet behavior.
     */
    private lateinit var behavior: BottomSheetBehavior<FrameLayout>

    /**
     * Our custom bottom sheet layout.
     */
    private lateinit var frame: FiltersBottomSheet

    /**
     * The callback to use on this [behavior].
     */
    private val callback = object : BottomSheetBehavior.BottomSheetCallback() {

        /**
         * Called when the bottom sheet is dragged.
         */
        override fun onSlide(bottomSheet: View, slideOffset: Float) {
            frame.onSlide(slideOffset)
        }

        /**
         * Called when the state of the bottom sheet changes. It mimics the functionality of the
         * default callback, as it's lost when we overwite the callback.
         */
        override fun onStateChanged(bottomSheet: View, newState: Int) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                cancel()
            }
        }

    }

    /**
     * Sets the content view on this sheet and applies our custom [callback].
     */
    override fun setContentView(view: View) {
        super.setContentView(view)
        frame = view as FiltersBottomSheet
        val bottomSheet = window.decorView.findViewById<View>(
                com.google.android.material.R.id.design_bottom_sheet) as FrameLayout
        behavior = BottomSheetBehavior.from(bottomSheet)
        behavior.setBottomSheetCallback(callback)
    }

}