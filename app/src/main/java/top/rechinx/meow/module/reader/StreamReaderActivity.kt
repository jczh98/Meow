package top.rechinx.meow.module.reader

import android.graphics.Point
import android.graphics.Rect
import android.support.v4.view.GestureDetectorCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.GestureDetector
import android.view.MotionEvent
import android.view.View
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.zoomable.ZoomableRecyclerView


class StreamReaderActivity: ReaderActivity() {

    private lateinit var mLayoutManager: LinearLayoutManager
    private var mLastPosition: Int = 0

    override fun initView() {
        super.initView()
        mAdapter.setReaderMode(ReaderAdapter.STREAM_READER_MODE)
        (mRecyclerView as ZoomableRecyclerView).isEnableScale = true
        (mRecyclerView as ZoomableRecyclerView).setOnViewTapListener(this)

        mRecyclerView.addOnScrollListener(object :RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    val firstItem = mLayoutManager.findFirstVisibleItemPosition()
                    if(firstItem == 0) {
                        mPresenter.loadPrev()
                    }
                    val lastItem = mLayoutManager.findLastVisibleItemPosition()
                    if(lastItem == mAdapter.itemCount - 1) {
                        mPresenter.loadNext()
                    }
                }
            }

            override fun onScrolled(recyclerView: RecyclerView?, dx: Int, dy: Int) {
                val target = mLayoutManager.findFirstVisibleItemPosition()
                if (target != mLastPosition) {
                    val newImage = mAdapter.getItem(target)
                    val oldImage = mAdapter.getItem(mLastPosition)

                    if (!oldImage.chapter.equals(newImage.chapter)) {
                        if (dy > 0) {
                            mPresenter.toNextChapter()
                        } else if (dy < 0) {
                            mPresenter.toPrevChapter()
                        }
                    }
                    currentPage = mAdapter.getItem(target).page_number
                    mLastPosition = target
                    updateProgress()
                }
            }
        })
    }

    override fun getLayoutManager(): LinearLayoutManager {
        mLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        return mLayoutManager
    }


    override fun prevPage() {
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        mRecyclerView.smoothScrollBy(0, -point.y)
        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
            mPresenter.loadPrev()
        }
    }

    override fun nextPage() {
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        mRecyclerView.smoothScrollBy(0, point.y)
        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
            mPresenter.loadNext()
        }
    }

    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        if(fromUser) {
            val current = mLastPosition + value - currentPage
            val pos = mAdapter.getPositionByNum(current, value, value < currentPage)
            mLayoutManager.scrollToPositionWithOffset(pos, 0)
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_stream_reader
}