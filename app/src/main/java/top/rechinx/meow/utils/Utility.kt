package top.rechinx.meow.utils

import android.content.Context

object Utility {

    fun dpToPixel(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

}