package top.rechinx.meow.module.reader

import android.support.v7.widget.LinearLayoutManager
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.support.rvp.RecyclerViewPager

class PageReaderActivity: ReaderActivity(), RecyclerViewPager.OnPageChangedListener {

    override fun initView() {
        super.initView()
        mAdapter.setReaderMode(ReaderAdapter.PAGE_READER_MODE)
        (mRecyclerView as RecyclerViewPager).setTriggerOffset(0.01f * 10)
        (mRecyclerView as RecyclerViewPager).setScrollSpeed(0.02f)
        (mRecyclerView as RecyclerViewPager).setOnPageChangedListener(this)
    }

    override fun getLayoutManager(): LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

    /**
     * chapter controller
     */
    override fun OnPageChanged(oldPosition: Int, newPosition: Int) {
        if(oldPosition < 0 || newPosition < 0) return

        if(newPosition == oldPosition) return

        if(newPosition == 0) {
            mPresenter.loadPrev()
        }

        if(newPosition == mAdapter.itemCount - 1) {
            mPresenter.loadNext()
        }

        val newImage = mAdapter.getItem(newPosition)
        val oldImage = mAdapter.getItem(oldPosition)
        if(!oldImage.chapter.equals(newImage.chapter)) {
            if(newPosition > oldPosition) {
                mPresenter.toNextChapter()
            }
            if(newPosition < oldPosition) {
                mPresenter.toPrevChapter()
            }
        }
        currentPage = newImage.page_number
        updateProgress()
    }

    /**
     * SeekBar progress changed
     */
    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        if(fromUser) {
            val current = getCurrentPosition() + value - currentPage
            val pos = mAdapter.getPositionByNum(current, value, value < currentPage)
            mRecyclerView.scrollToPosition(pos)
        }
    }

    /**
     * PhotoView touch event
     */

//    override fun onCenter() {
//        switchControl()
//    }
//
//    override fun onPrev() {
//        val cur = getCurrentPosition()
//        if(cur - 1 >= 0) mRecyclerView.smoothScrollToPosition(cur - 1)
//    }
//
//    override fun onNext() {
//        val cur = getCurrentPosition()
//        if(cur + 1 < mAdapter.itemCount) mRecyclerView.smoothScrollToPosition(cur + 1)
//    }

    private fun getCurrentPosition(): Int = (mRecyclerView as RecyclerViewPager).currentPosition

    override fun prevPage() {
        val cur = getCurrentPosition()
        if(cur - 1 >= 0) mRecyclerView.smoothScrollToPosition(cur - 1)
    }

    override fun nextPage() {
        val cur = getCurrentPosition()
        if(cur + 1 < mAdapter.itemCount) mRecyclerView.smoothScrollToPosition(cur + 1)
    }

    override fun getLayoutId(): Int = R.layout.activity_page_reader
}