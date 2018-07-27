package top.rechinx.meow.widget

import android.content.Context
import android.content.res.ColorStateList
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.support.v7.widget.AppCompatTextView
import android.util.AttributeSet
import top.rechinx.meow.utils.Utility
import top.rechinx.meow.R

class ChapterButton @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyle: Int = 0) : AppCompatTextView(context, attrs, defStyle) {

    private var normalColor: Int = 0
    private var accentColor: Int = 0
    private var download: Boolean = false

    init {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet?) {
        val typedArray = context.theme.obtainStyledAttributes(
                attrs, R.styleable.ChapterButton, 0, 0)
        accentColor = typedArray.getColor(R.styleable.ChapterButton_selected_color, Color.BLACK)
        typedArray.recycle()

        normalColor = -0x76000000

        isClickable = true
        download = false
        initColorDrawableState()
        initDrawableState()
    }

    private fun initColorDrawableState() {
        val colorStateList = ColorStateList(arrayOf(NORMAL_STATE, SELECTED_STATE),
                intArrayOf(normalColor, Color.WHITE))
        setTextColor(colorStateList)
    }

    private fun initDrawableState() {
        val normalDrawable = GradientDrawable()
        normalDrawable.setStroke(Utility.dpToPixel(1f, context).toInt(), normalColor)
        normalDrawable.cornerRadius = Utility.dpToPixel(18f, context)
        normalDrawable.setColor(Color.TRANSPARENT)

        val selectedDrawable = GradientDrawable()
        selectedDrawable.setStroke(Utility.dpToPixel(1f, context).toInt(), accentColor)
        selectedDrawable.cornerRadius = Utility.dpToPixel(18f, context)
        selectedDrawable.setColor(accentColor)

        val stateList = StateListDrawable()
        stateList.addState(NORMAL_STATE, normalDrawable)
        stateList.addState(SELECTED_STATE, selectedDrawable)
        setBackgroundDrawable(stateList)
    }

    fun setDownload(download: Boolean) {
        if (this.download != download) {
            this.download = download
            normalColor = if (download) accentColor else -0x76000000
            initColorDrawableState()
            initDrawableState()
        }
    }

    companion object {

        private val NORMAL_STATE = intArrayOf(-android.R.attr.state_selected)
        private val SELECTED_STATE = intArrayOf(android.R.attr.state_selected)
    }

}
