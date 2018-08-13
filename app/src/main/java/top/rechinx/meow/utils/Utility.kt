package top.rechinx.meow.utils

import android.content.Context
import java.text.SimpleDateFormat
import java.util.*

object Utility {

    fun dpToPixel(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun getFormatTime(format: String, time: Long): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(Date(time))
    }
}