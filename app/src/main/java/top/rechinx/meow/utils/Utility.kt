package top.rechinx.meow.utils

import android.content.Context
import android.graphics.Matrix
import android.os.Build
import android.view.View
import java.text.SimpleDateFormat
import java.util.*
import android.util.DisplayMetrics
import android.view.WindowManager



object Utility {

    fun dpToPixel(dp: Float, context: Context): Float {
        return dp * context.resources.displayMetrics.density
    }

    fun getFormatTime(format: String, time: Long): String {
        return SimpleDateFormat(format, Locale.getDefault()).format(Date(time))
    }

    fun calculateScale(matrix: Matrix): Float {
        val values = FloatArray(9)
        matrix.getValues(values)
        return Math.sqrt((Math.pow(values[Matrix.MSCALE_X].toDouble(), 2.0).toFloat() + Math.pow(values[Matrix.MSKEW_Y].toDouble(), 2.0).toFloat()).toDouble()).toFloat()
    }

    fun getViewWidth(view: View): Int {
        return view.width - view.paddingLeft - view.paddingRight
    }

    fun getViewHeight(view: View): Int {
        return view.height - view.paddingTop - view.paddingBottom
    }

    @JvmStatic fun getScreenHeight(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.heightPixels
    }

    @JvmStatic fun getScreenWidth(context: Context): Int {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val outMetrics = DisplayMetrics()
        wm.defaultDisplay.getMetrics(outMetrics)
        return outMetrics.widthPixels
    }

    fun postOnAnimation(view: View, runnable: Runnable) {
        if (Build.VERSION.SDK_INT >= 16) {
            view.postOnAnimation(runnable)
        } else {
            view.postDelayed(runnable, 16L)
        }
    }
}