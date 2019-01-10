package top.rechinx.meow.ui.widget

import android.content.Context
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatTextView
import timber.log.Timber
import java.util.jar.Attributes

class ChapterButton @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyle: Int = 0
) : AppCompatTextView(context, attrs, defStyle) {

    var primaryColor : Int = Color.BLUE
        set(value) {
            field = value
            initColorDrawableState()
            initDrawableState()
        }

    var customTextColor: Int = Color.BLACK
        set(value) {
            field = value
            initColorDrawableState()
            initDrawableState()
        }

    init {
        initColorDrawableState()
        initDrawableState()
    }

    private fun initColorDrawableState() {
        val colorStateList = ColorStateList(arrayOf(NORMAL_STATE, SELECTED_STATE),
                intArrayOf(customTextColor, Color.WHITE))
        setTextColor(colorStateList)
    }

    private fun initDrawableState() {
        val normalDrawable = GradientDrawable()
        normalDrawable.setStroke(dpToPixel(1f, context).toInt(), primaryColor)
        normalDrawable.cornerRadius = dpToPixel(18f, context)
        normalDrawable.setColor(Color.TRANSPARENT)

        val selectedDrawable = GradientDrawable()
        selectedDrawable.setStroke(dpToPixel(1f, context).toInt(), primaryColor)
        selectedDrawable.cornerRadius = dpToPixel(18f, context)
        selectedDrawable.setColor(primaryColor)

        val stateList = StateListDrawable()
        stateList.addState(NORMAL_STATE, normalDrawable)
        stateList.addState(SELECTED_STATE, selectedDrawable)
        setBackgroundDrawable(stateList)
    }

    private fun dpToPixel(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    companion object {
        private val NORMAL_STATE = intArrayOf(-android.R.attr.state_selected)
        private val SELECTED_STATE = intArrayOf(android.R.attr.state_selected)
    }
}