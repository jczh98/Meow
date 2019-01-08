package top.rechinx.meow.ui.base

import android.app.Activity
import android.content.ContextWrapper
import android.content.res.ColorStateList
import android.util.AttributeSet
import androidx.appcompat.view.ContextThemeWrapper
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.forEach
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.jaredrummler.cyanea.Cyanea
import com.jaredrummler.cyanea.inflator.CyaneaViewProcessor
import com.jaredrummler.cyanea.utils.ColorUtils
import top.rechinx.meow.R
import top.rechinx.meow.rikka.ext.getResourceId

fun Activity.getCyaneaViewProcessors(): Array<CyaneaViewProcessor<*>> {
    return arrayOf(
            BottomNavigationViewProcessor(),
            ToolbarViewProcessor()
    )
}

/**
 * CyaneaViewProcessor for [Toolbar]
 */
private class ToolbarViewProcessor : CyaneaViewProcessor<Toolbar>() {
    override fun getType(): Class<Toolbar> = Toolbar::class.java

    override fun process(view: Toolbar, attrs: AttributeSet?, cyanea: Cyanea) {
        // Styles the toolbar menu
        view.post {
            view.context?.let { context ->
                (context as? Activity)?.run {
                    cyanea.tint(view.menu, this)
                } ?: ((context as? ContextWrapper)?.baseContext as? Activity)?.run {
                    cyanea.tint(view.menu, this)
                }
            }
        }
    }

}

/**
 * CyaneaViewProcessor for [BottomNavigationView]
 */
private class BottomNavigationViewProcessor : CyaneaViewProcessor<BottomNavigationView>() {

    override fun getType(): Class<BottomNavigationView> = BottomNavigationView::class.java

    override fun process(view: BottomNavigationView, attrs: AttributeSet?, cyanea: Cyanea) {
        val checkedColor = if (ColorUtils.isLightColor(cyanea.primary, 1.0)) {
            ContextCompat.getColor(view.context, R.color.md_black_1000)
        } else {
            cyanea.primary
        }
        val uncheckedColor = ContextCompat.getColor(view.context, R.color.md_grey_600)

        val colorState = ColorStateList(
                arrayOf(
                        intArrayOf(-android.R.attr.state_checked),
                        intArrayOf()
                ),
                intArrayOf(uncheckedColor, checkedColor)
        )

        val itemBackgroundRes = ContextThemeWrapper(view.context, if (cyanea.isActionBarLight) {
            R.style.Theme_MaterialComponents_Light_NoActionBar
        } else {
            R.style.Theme_MaterialComponents_NoActionBar
        }).getResourceId(R.attr.selectableItemBackgroundBorderless)

        view.setBackgroundColor(cyanea.backgroundColor)
        view.itemIconTintList = colorState
        view.itemTextColor = colorState
        view.itemBackgroundResource = itemBackgroundRes
    }

}