package top.rechinx.meow.widget.zoomablerv

import android.content.Context
import android.graphics.RectF
import android.support.v4.widget.ScrollerCompat
import android.view.View

import top.rechinx.meow.utils.Utility

/**
 * Created by Hiroshi on 2017/5/27.
 */

internal class FlingRunnable(context: Context, private val mListener: OnFlingRunningListener, private val mView: View) : Runnable {
    private val mScroller: ScrollerCompat
    private var mCurrentX: Int = 0
    private var mCurrentY: Int = 0

    init {
        mScroller = ScrollerCompat.create(context)
    }

    fun cancelFling() {
        mScroller.abortAnimation()
    }

    fun fling(rect: RectF, viewWidth: Int, viewHeight: Int, velocityX: Int, velocityY: Int) {
        val startX = Math.round(-rect.left)
        val minX: Int
        val maxX: Int
        val minY: Int
        val maxY: Int

        if (viewWidth < rect.width()) {
            minX = 0
            maxX = Math.round(rect.width() - viewWidth)
        } else {
            maxX = startX
            minX = maxX
        }

        val startY = Math.round(-rect.top)
        if (viewHeight < rect.height()) {
            minY = 0
            maxY = Math.round(rect.height() - viewHeight)
        } else {
            maxY = startY
            minY = maxY
        }

        mCurrentX = startX
        mCurrentY = startY

        if (startX != maxX || startY != maxY) {
            mScroller.fling(startX, startY, velocityX, velocityY, minX, maxX, minY, maxY, 0, 0)
        }
    }

    override fun run() {
        if (mScroller.isFinished) {
            return
        }

        if (mScroller.computeScrollOffset()) {
            val newX = mScroller.currX
            val newY = mScroller.currY
            mListener.onFlingRunning(mCurrentX - newX, mCurrentY - newY)
            mCurrentX = newX
            mCurrentY = newY
            Utility.postOnAnimation(mView, this)
        }
    }

    internal interface OnFlingRunningListener {
        fun onFlingRunning(dx: Int, dy: Int)
    }

}
