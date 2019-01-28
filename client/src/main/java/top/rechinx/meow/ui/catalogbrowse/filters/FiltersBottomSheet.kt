package top.rechinx.meow.ui.catalogbrowse.filters

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.FrameLayout
import androidx.core.view.doOnPreDraw
import com.google.android.material.bottomsheet.BottomSheetBehavior

/**
 * A custom bottom sheet layout for the list of filters which allows a sticky footer for the set of
 * actions (close, reset, search). It dynamically sets the translationY property on the footer view
 * when the sheet is scrolled.
 */
class FiltersBottomSheet @JvmOverloads constructor(
        context: Context,
        attributeSet: AttributeSet? = null
) : FrameLayout(context, attributeSet) {

    /**
     * The footer view. It's the last view of this ViewGroup and it's set on the first [onMeasure].
     */
    private var footer: View? = null

    /**
     * The offset of the sheet used to position the [footer].
     */
    private var maxOffset = 0f

    /**
     * The original bottom padding of the content (the view before [footer]) set from xml.
     */
    private var contentPaddingBottom: Int? = null

    /**
     * Called when the view is being measured. It calls the super method, retrieves the [footer] view
     * and applies our bottom padding to the content view.
     */
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        // The sticky footer is the last view in the group
        val footer = footer ?: getChildAt(childCount - 1).also { footer = it }

        // The content is the second to last view in the group
        val content = getChildAt(childCount - 2)

        // Get or store the original padding bottom
        val contentPaddingBottom = contentPaddingBottom ?: content.paddingBottom
                .also { contentPaddingBottom = it }

        // Apply padding to content
        content.setPadding(content.paddingLeft, content.paddingTop, content.paddingRight,
                footer.measuredHeight + contentPaddingBottom)
    }

    /**
     * Called when the view is layout. It calculates the offset and applies the footer's translationY
     * depending on the current sheet state.
     */
    override fun onLayout(changed: Boolean, left: Int, top: Int, right: Int, bottom: Int) {
        super.onLayout(changed, left, top, right, bottom)

        val footer = footer!! // Can't be null since it was retrieved when measuring

        val frame = parent as View
        val coordinator = frame.parent as View
        val behavior = BottomSheetBehavior.from(frame)

        // Since the max offset is measured after layout, wait for the first draw event to retrieve
        // the value and set the initial state
        frame.doOnPreDraw {
            maxOffset = (FiltersBottomSheet.Companion.maxOffsetField.get(behavior) as Int).toFloat() - (coordinator.height - height)

            when (behavior.state) {
                BottomSheetBehavior.STATE_EXPANDED -> footer.translationY = 0f
                BottomSheetBehavior.STATE_COLLAPSED -> footer.translationY = -maxOffset
            }
        }
    }

    /**
     * Called when the bottom sheet is being dragged. It ensures that the footer is attached to the
     * bottom of the screen.
     */
    fun onSlide(slideOffset: Float) {
        footer?.translationY = if (slideOffset >= 0) {
            -maxOffset * (1 - slideOffset)
        } else {
            -maxOffset
        }
    }

    private companion object {
        /**
         * The offset field of the bottom sheet.
         */
        val maxOffsetField = BottomSheetBehavior::class.java.getDeclaredField("collapsedOffset").apply {
            isAccessible = true
        }
    }

}