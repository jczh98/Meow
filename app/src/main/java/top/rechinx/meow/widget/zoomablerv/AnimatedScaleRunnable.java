package top.rechinx.meow.widget.zoomablerv;

import android.graphics.Matrix;
import android.support.v7.widget.ViewUtils;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import top.rechinx.meow.utils.Utility;

/**
 * Created by Hiroshi on 2017/5/27.
 */

class AnimatedScaleRunnable implements Runnable {

    private final View mView;
    private final Matrix mMatrix;
    private final float mFocusX, mFocusY;
    private final long mStartTime;
    private final float mScaleStart, mScaleEnd;
    private final OnScaleDragGestureListener mListener;
    private final Interpolator mZoomInterpolator = new AccelerateDecelerateInterpolator();

    AnimatedScaleRunnable(float scale, float x, float y, View view, Matrix matrix,
                          OnScaleDragGestureListener listener) {
        mFocusX = x;
        mFocusY = y;
        mStartTime = System.currentTimeMillis();
        mScaleStart = Utility.calculateScale(matrix);
        mScaleEnd = scale;
        mView = view;
        mMatrix = matrix;
        mListener = listener;
    }

    @Override
    public void run() {
        float t = interpolate();
        float scale = mScaleStart + t * (mScaleEnd - mScaleStart);
        float deltaScale = scale / Utility.calculateScale(mMatrix);

        mListener.onScale(deltaScale, mFocusX, mFocusY);

        if (t < 1f) {
            Utility.postOnAnimation(mView, this);
        }
    }

    private float interpolate() {
        float t = 1f * (System.currentTimeMillis() - mStartTime) / 200;
        t = Math.min(1f, t);
        t = mZoomInterpolator.getInterpolation(t);
        return t;
    }

}
