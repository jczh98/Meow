package top.rechinx.meow.widget


import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.Button
import android.widget.Checkable

class CheckableButton : Button, Checkable {
    private var mChecked: Boolean = false

    private var mBroadcasting: Boolean = false

    private val mOnCheckedChangeListener: OnCheckedChangeListener? = null
    private var mOnCheckedChangeWidgetListener: OnCheckedChangeListener? = null

    constructor(context: Context) : super(context) {}

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {}

    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {}

    override fun onCreateDrawableState(extraSpace: Int): IntArray {
        val drawableState = super.onCreateDrawableState(extraSpace + 1)
        if (mChecked) {
            View.mergeDrawableStates(drawableState, CHECKED_STATE_LIST)
        }
        return drawableState
    }

    override fun setChecked(checked: Boolean) {
        if (mChecked != checked) {
            mChecked = checked
            refreshDrawableState()

            if (mBroadcasting) {
                return
            }

            mBroadcasting = true
            mOnCheckedChangeListener?.onCheckedChanged(this, mChecked)
            if (mOnCheckedChangeWidgetListener != null) {
                mOnCheckedChangeWidgetListener!!.onCheckedChanged(this, mChecked)
            }
        }
    }

    override fun isChecked(): Boolean {
        return mChecked
    }

    override fun toggle() {
        isChecked = !mChecked
    }

    override fun performClick(): Boolean {
        toggle()
        return super.performClick()
    }

    internal fun setOnCheckedChangeWidgetListener(listener: OnCheckedChangeListener) {
        mOnCheckedChangeWidgetListener = listener
    }

    interface OnCheckedChangeListener {
        fun onCheckedChanged(buttonView: CheckableButton, isChecked: Boolean)
    }

    companion object {
        private val CHECKED_STATE_LIST = intArrayOf(android.R.attr.state_checked)
    }

}