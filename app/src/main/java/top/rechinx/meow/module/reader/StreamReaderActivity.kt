package top.rechinx.meow.module.reader

import android.graphics.Rect
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.widget.zoomablerv.ZoomableRecyclerView

class StreamReaderActivity: ReaderActivity() {

    override fun initView() {
        super.initView()
        mAdapter.setReaderMode(ReaderAdapter.STREAM_READER_MODE)
        mRecyclerView.addItemDecoration( object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                    outRect.set(0, 0, 0, 0)
            }
        })
        (mRecyclerView as ZoomableRecyclerView).setScaleFactor(200 * 0.01f)
        (mRecyclerView as ZoomableRecyclerView).setVertical(true)
        (mRecyclerView as ZoomableRecyclerView).setDoubleTap(false)
    }

    override fun getLayoutManager(): LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

    override fun onCenter() {
    }

    override fun onPrev() {
    }

    override fun onNext() {
    }

    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
    }

    override fun getLayoutId(): Int = R.layout.activity_stream_reader
}