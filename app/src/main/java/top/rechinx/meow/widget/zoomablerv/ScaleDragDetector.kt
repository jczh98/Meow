package top.rechinx.meow.widget.zoomablerv

import android.content.Context
import android.os.Build
import android.view.MotionEvent
import android.view.ScaleGestureDetector
import android.view.VelocityTracker
import android.view.ViewConfiguration

/**
 * ****************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * *****************************************************************************
 */
class ScaleDragDetector(context: Context, private val mListener: OnScaleDragGestureListener) : ScaleGestureDetector.OnScaleGestureListener {

    private var mActivePointerId = INVALID_POINTER_ID
    private var mActivePointerIndex = 0
    private val mDetector: ScaleGestureDetector

    private var mVelocityTracker: VelocityTracker? = null
    var isDragging: Boolean = false
        private set
    private var mLastTouchX: Float = 0.toFloat()
    private var mLastTouchY: Float = 0.toFloat()
    private val mTouchSlop: Float
    private val mMinimumVelocity: Float

    val isScaling: Boolean
        get() = mDetector.isInProgress

    init {
        val configuration = ViewConfiguration
                .get(context)
        mMinimumVelocity = configuration.scaledMinimumFlingVelocity.toFloat()
        mTouchSlop = configuration.scaledTouchSlop.toFloat()
        mDetector = ScaleGestureDetector(context, this)
        if (Build.VERSION.SDK_INT >= 19) {
            mDetector.isQuickScaleEnabled = false
        }
    }

    override fun onScale(detector: ScaleGestureDetector): Boolean {
        val scaleFactor = detector.scaleFactor

        if (java.lang.Float.isNaN(scaleFactor) || java.lang.Float.isInfinite(scaleFactor))
            return false

        mListener.onScale(scaleFactor, detector.focusX, detector.focusY)
        return true
    }

    override fun onScaleBegin(detector: ScaleGestureDetector): Boolean {
        return true
    }

    override fun onScaleEnd(detector: ScaleGestureDetector) {
        mListener.onScaleEnd()
    }

    private fun getActiveX(ev: MotionEvent): Float {
        try {
            return ev.getX(mActivePointerIndex)
        } catch (e: Exception) {
            return ev.x
        }

    }

    private fun getActiveY(ev: MotionEvent): Float {
        try {
            return ev.getY(mActivePointerIndex)
        } catch (e: Exception) {
            return ev.y
        }

    }

    fun onTouchEvent(ev: MotionEvent): Boolean {
        try {
            mDetector.onTouchEvent(ev)
            return processTouchEvent(ev)
        } catch (e: IllegalArgumentException) {
            return true
        }

    }

    private fun processTouchEvent(ev: MotionEvent): Boolean {
        val action = ev.action
        when (action and MotionEvent.ACTION_MASK) {
            MotionEvent.ACTION_DOWN -> {
                mActivePointerId = ev.getPointerId(0)

                mVelocityTracker = VelocityTracker.obtain()
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.addMovement(ev)
                }

                mLastTouchX = getActiveX(ev)
                mLastTouchY = getActiveY(ev)
                isDragging = false
            }
            MotionEvent.ACTION_MOVE -> {
                val x = getActiveX(ev)
                val y = getActiveY(ev)
                val dx = x - mLastTouchX
                val dy = y - mLastTouchY

                if (!isDragging) {
                    isDragging = Math.sqrt((dx * dx + dy * dy).toDouble()) >= mTouchSlop
                }

                if (isDragging) {
                    mListener.onDrag(dx, dy)
                    mLastTouchX = x
                    mLastTouchY = y

                    if (null != mVelocityTracker) {
                        mVelocityTracker!!.addMovement(ev)
                    }
                }
            }
            MotionEvent.ACTION_CANCEL -> {
                mActivePointerId = INVALID_POINTER_ID
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_UP -> {
                mActivePointerId = INVALID_POINTER_ID
                if (isDragging) {
                    if (null != mVelocityTracker) {
                        mLastTouchX = getActiveX(ev)
                        mLastTouchY = getActiveY(ev)

                        mVelocityTracker!!.addMovement(ev)
                        mVelocityTracker!!.computeCurrentVelocity(1000)

                        val vX = mVelocityTracker!!.xVelocity
                        val vY = mVelocityTracker!!
                                .yVelocity

                        if (Math.max(Math.abs(vX), Math.abs(vY)) >= mMinimumVelocity) {
                            mListener.onFling(mLastTouchX, mLastTouchY, -vX,
                                    -vY)
                        }
                    }
                }
                if (null != mVelocityTracker) {
                    mVelocityTracker!!.recycle()
                    mVelocityTracker = null
                }
            }
            MotionEvent.ACTION_POINTER_UP -> {
                val pointerIndex = ev.action and MotionEvent.ACTION_POINTER_INDEX_MASK shr MotionEvent.ACTION_POINTER_INDEX_SHIFT
                val pointerId = ev.getPointerId(pointerIndex)
                if (pointerId == mActivePointerId) {
                    val newPointerIndex = if (pointerIndex == 0) 1 else 0
                    mActivePointerId = ev.getPointerId(newPointerIndex)
                    mLastTouchX = ev.getX(newPointerIndex)
                    mLastTouchY = ev.getY(newPointerIndex)
                }
            }
        }

        mActivePointerIndex = ev
                .findPointerIndex(if (mActivePointerId != INVALID_POINTER_ID)
                    mActivePointerId
                else
                    0)
        return true
    }

    companion object {

        private val INVALID_POINTER_ID = -1
    }

}
