package top.rechinx.meow.widget

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.TintTypedArray
import androidx.core.view.ViewCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.internal.ScrimInsetsFrameLayout
import com.google.android.material.R

@SuppressLint("RestrictedApi")
class MaterialSideSheet @JvmOverloads constructor(
        context: Context,
        attrs: AttributeSet? = null,
        defStyleAttr: Int = 0
) : ScrimInsetsFrameLayout(context, attrs, defStyleAttr) {

    lateinit var contentView: View

    /**
     * Max width of side sheet
     */
    private var maxWidth: Int

    init {
        val a = TintTypedArray.obtainStyledAttributes(context, attrs,
                R.styleable.NavigationView, defStyleAttr,
                R.style.Widget_Design_NavigationView)

        ViewCompat.setBackground(
                this, a.getDrawable(R.styleable.NavigationView_android_background))

        if (a.hasValue(R.styleable.NavigationView_elevation)) {
            ViewCompat.setElevation(this, a.getDimensionPixelSize(
                    R.styleable.NavigationView_elevation, 0).toFloat())
        }

        ViewCompat.setFitsSystemWindows(this,
                a.getBoolean(R.styleable.NavigationView_android_fitsSystemWindows, false))

        maxWidth = a.getDimensionPixelSize(R.styleable.NavigationView_android_maxWidth, 0)

        a.recycle()

    }

    /**
     * Overriden to measure the width of the navigation view.
     */
    @SuppressLint("SwitchIntDef")
    override fun onMeasure(widthSpec: Int, heightSpec: Int) {
        val width = when (MeasureSpec.getMode(widthSpec)) {
            MeasureSpec.AT_MOST -> MeasureSpec.makeMeasureSpec(
                    Math.min(MeasureSpec.getSize(widthSpec), maxWidth), MeasureSpec.EXACTLY)
            MeasureSpec.UNSPECIFIED -> MeasureSpec.makeMeasureSpec(maxWidth, MeasureSpec.EXACTLY)
            else -> widthSpec
        }
        super.onMeasure(width, heightSpec)
    }

    fun setContentView(resId: Int) {
        contentView = LayoutInflater.from(context).inflate(resId, this, false)
        addView(contentView)
    }

}