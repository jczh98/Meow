package top.rechinx.meow.module.reader

import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.support.zoomable.ZoomableRecyclerView

class StreamReaderFragment: ReaderFragment() {

    @BindView(R.id.reader_recycler_view) lateinit var mRecyclerView: ZoomableRecyclerView
    private lateinit var mLayoutManager: LinearLayoutManager

    private var mLastPosition: Int = 0
    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_stream_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter.setReaderMode(ReaderAdapter.STREAM_READER_MODE)
        mRecyclerView.isEnableScale = true
        mRecyclerView.setOnViewTapListener(activity as ReaderActivity)
        mRecyclerView.adapter = mAdapter
        mLayoutManager = LinearLayoutManager(view.context, LinearLayoutManager.VERTICAL, false)
        mRecyclerView.layoutManager = mLayoutManager
        mRecyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {

            override fun onScrollStateChanged(recyclerView: RecyclerView?, newState: Int) {
                if(newState == RecyclerView.SCROLL_STATE_IDLE || newState == RecyclerView.SCROLL_STATE_SETTLING) {
                    val firstItem = mLayoutManager.findFirstVisibleItemPosition()
                    if(firstItem == 0) {
                        onLoadPrevChapter()
                    }
                    val lastItem = mLayoutManager.findLastVisibleItemPosition()
                    if(lastItem == mAdapter.itemCount - 1) {
                        onLoadNextChapter()
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
                            onNextChapter()
                        } else if (dy < 0) {
                            onPrevChapter()
                        }
                    }
                    onReaderPageChanged(mAdapter.getItem(target).page_number)
                    mLastPosition = target
                }
            }
        })
    }

    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        if(fromUser) {
            val currentPage = getCurrentPage()
            val current = mLastPosition + value - currentPage
            val pos = mAdapter.getPositionByNum(current, value, value < currentPage)
            mLayoutManager.scrollToPositionWithOffset(pos, 0)
        }
    }

    override fun prevPage() {
        val point = Point()
        activity?.windowManager?.defaultDisplay?.getSize(point)
        mRecyclerView.smoothScrollBy(0, -point.y)
        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
            onLoadPrevChapter()
        }
    }

    override fun nextPage() {
        val point = Point()
        activity?.windowManager?.defaultDisplay?.getSize(point)
        mRecyclerView.smoothScrollBy(0, point.y)
        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
            onLoadNextChapter()
        }
    }

    override fun scrollToPosition(page: Int) {
        mRecyclerView.scrollToPosition(page)
    }

    override fun getCurrentItemPosition(): Int = mLastPosition
}