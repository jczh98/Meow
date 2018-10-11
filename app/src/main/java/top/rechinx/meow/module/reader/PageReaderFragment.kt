package top.rechinx.meow.module.reader

import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import com.github.chrisbanes.photoview.OnViewTapListener
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.rvp.RecyclerViewPager

class PageReaderFragment: ReaderFragment(), RecyclerViewPager.OnPageChangedListener {

    @BindView(R.id.reader_recycler_view) lateinit var mRecyclerView: RecyclerViewPager

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_page_reader, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mAdapter.setReaderMode(ReaderAdapter.PAGE_READER_MODE)
        mAdapter.setOnViewTapListener(activity as ReaderActivity)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.layoutManager = LinearLayoutManager(view.context, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.setTriggerOffset(0.01f * 10)
        mRecyclerView.setScrollSpeed(0.02f)
        mRecyclerView.setOnPageChangedListener(this)
    }

    /**
     * chapter controller
     */
    override fun OnPageChanged(oldPosition: Int, newPosition: Int) {
        if(oldPosition < 0 || newPosition < 0) return

        if(newPosition == oldPosition) return

        if(newPosition == 0) {
            onLoadPrevChapter()
            //mPresenter.loadPrev()
        }

        if(newPosition == mAdapter.itemCount - 1) {
            onLoadNextChapter()
            //mPresenter.loadNext()
        }

        val newImage = mAdapter.getItem(newPosition)
        val oldImage = mAdapter.getItem(oldPosition)
        if(!oldImage.chapter.equals(newImage.chapter)) {
            if(newPosition > oldPosition) {
                //mPresenter.toNextChapter()
                onNextChapter()
            }
            if(newPosition < oldPosition) {
                //mPresenter.toPrevChapter()
                onPrevChapter()
            }
        }
        onReaderPageChanged(newImage.page_number)
        //currentPage = newImage.page_number
        //updateProgress()
    }

    /**
     * SeekBar progress changed
     */
    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        if(fromUser) {
            val currentPage = getCurrentPage()
            val current = mRecyclerView.currentPosition + value - currentPage
            val pos = mAdapter.getPositionByNum(current, value, value < currentPage)
            mRecyclerView.scrollToPosition(pos)
        }
    }

    private fun getCurrentPosition(): Int = mRecyclerView.currentPosition

    override fun prevPage() {
        val cur = getCurrentPosition()
        if(cur - 1 >= 0) mRecyclerView.smoothScrollToPosition(cur - 1)
    }

    override fun nextPage() {
        val cur = getCurrentPosition()
        if(cur + 1 < mAdapter.itemCount) mRecyclerView.smoothScrollToPosition(cur + 1)
    }

    override fun scrollToPosition(page: Int) {
        mRecyclerView.scrollToPosition(page)
    }

    override fun getCurrentItemPosition(): Int = mRecyclerView.currentPosition
}