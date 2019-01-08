package top.rechinx.meow.rikka.ext

import android.content.Context
import androidx.annotation.AttrRes
import androidx.core.content.res.getResourceIdOrThrow

fun Context.getResourceId(@AttrRes resource: Int): Int {
    val typedArray = obtainStyledAttributes(intArrayOf(resource))
    val drawable = typedArray.getResourceIdOrThrow(0)
    typedArray.recycle()
    return drawable
}