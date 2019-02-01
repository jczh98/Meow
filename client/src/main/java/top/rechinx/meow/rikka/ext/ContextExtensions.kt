package top.rechinx.meow.rikka.ext

import android.content.Context
import android.content.res.Resources
import androidx.annotation.AttrRes
import androidx.core.content.res.getResourceIdOrThrow

val Int.dpToPx: Int
    get() = (this * Resources.getSystem().displayMetrics.density).toInt()

fun Context.getResourceId(@AttrRes resource: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(resource))
    val drawable = typedArray.getResourceIdOrThrow(0)
    typedArray.recycle()
    return drawable
}

fun Context.getResourceColor(@AttrRes resource: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(resource))
    val attrValue = typedArray.getColor(0, 0)
    typedArray.recycle()
    return attrValue
}