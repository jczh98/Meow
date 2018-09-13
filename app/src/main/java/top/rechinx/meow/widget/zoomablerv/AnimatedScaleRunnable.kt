package top.rechinx.meow.widget.zoomablerv

import android.graphics.Matrix
import android.support.v7.widget.ViewUtils
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.view.animation.Interpolator

import top.rechinx.meow.utils.Utility

/**
 * Created by Hiroshi on 2017/5/27.
 */

internal class AnimatedScaleRunnable(private val mScaleEnd: Float, private val mFocusX: Float, private val mFocusY: Float, private val mView: View, private val mMatrix: Matrix,
                                     private val mListener: OnScaleDragGestureListener) : Runnable {
    private val mStartTime: Long
    private val mScaleStart: Float
    private val mZoomInterpolator = AccelerateDecelerateInterpolator()

    init {
        mStartTime = System.currentTimeMillis()
        mScaleStart = Utility.calculateScale(mMatrix)
    }

    override fun run() {
        val t = interpolate()
        val scale = mScaleStart + t * (mScaleEnd - mScaleStart)
        val deltaScale = scale / Utility.calculateScale(mMatrix)

        mListener.onScale(deltaScale, mFocusX, mFocusY)

        if (t < 1f) {
            Utility.postOnAnimation(mView, this)
        }
    }

    private fun interpolate(): Float {
        var t = 1f * (System.currentTimeMillis() - mStartTime) / 200
        t = Math.min(1f, t)
        t = mZoomInterpolator.getInterpolation(t)
        return t
    }

}
