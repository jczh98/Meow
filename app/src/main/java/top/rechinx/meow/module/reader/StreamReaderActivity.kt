package top.rechinx.meow.module.reader

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.widget.zoomablerv.ZoomRecyclerView


class StreamReaderActivity: ReaderActivity() {

    private lateinit var mLayoutManager: LinearLayoutManager
    private var mLastPosition: Int = 0

    override fun initView() {
        super.initView()
        mAdapter.setReaderMode(ReaderAdapter.STREAM_READER_MODE)
        mRecyclerView.addItemDecoration( object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                    outRect.set(0, 0, 0, 0)
            }
        })
//        (mRecyclerView as ZoomRecyclerView).setScaleFactor(200 * 0.01f)
//        (mRecyclerView as ZoomRecyclerView).setVertical(true)
//        (mRecyclerView as ZoomRecyclerView).setDoubleTap(true)
        (mRecyclerView as ZoomRecyclerView).isEnableScale = true
        (mRecyclerView as ZoomRecyclerView).setTouchListener(object : ZoomRecyclerView.OnTouchListener {
            override fun clickScreen(x: Float, y: Float) {
                if (x<0.336){
                    onPrev()
                }else if(x<0.666){
                    onCenter()
                }else {
                    onNext()
                }
            }

        })
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

    override fun onCenter() {
        switchControl()
    }

    override fun onPrev() {
        val current = mLastPosition - 1
        val pos = mAdapter.getPositionByNum(current, currentPage - 1, true)
        mLayoutManager.scrollToPositionWithOffset(pos, 0)
        if (mLayoutManager.findFirstVisibleItemPosition() == 0) {
            mPresenter.loadPrev()
        }
    }

    override fun onNext() {
        val current = mLastPosition + 1
        val pos = mAdapter.getPositionByNum(current, currentPage + 1, false)
        mLayoutManager.scrollToPositionWithOffset(pos, 0)
        if (mLayoutManager.findLastVisibleItemPosition() == mAdapter.itemCount - 1) {
            mPresenter.loadNext()
        }
    }

    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        val current = mLastPosition + value - currentPage
        val pos = mAdapter.getPositionByNum(current, value, value < currentPage)
        mLayoutManager.scrollToPositionWithOffset(pos, 0)
    }

    override fun getLayoutId(): Int = R.layout.activity_stream_reader
}