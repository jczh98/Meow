package top.rechinx.meow.module.reader

import android.media.Image
import android.os.Bundle
import android.support.annotation.CallSuper
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BaseFragment

abstract class ReaderFragment: Fragment(), ReaderCallback {

    private var mRestored = false
    protected lateinit var mAdapter: ReaderAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ButterKnife.bind(this, view)
        mAdapter = ReaderAdapter(view.context, ArrayList())
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        val args = arguments
        if(!mRestored && args != null) {
            onRestoreState(args)
            mRestored = true
        }
    }

    @CallSuper
    override fun onSaveInstanceState(outState: Bundle) {
        val currentPosition = getCurrentItemPosition()
        outState.putInt("position", currentPosition)
        outState.putParcelableArrayList("imageurls", mAdapter.mData)
    }

    @CallSuper
    fun onRestoreState(savedState: Bundle) {
        val pages = savedState.getParcelableArrayList<ImageUrl>("imageurls")
        if (pages != null) {
            mAdapter.clear()
            mAdapter.addAll(pages)
            val currentPosition = savedState.getInt("position", 0)
            if (currentPosition != 0) {
                scrollToPosition(currentPosition)
            }
        }
    }

    override fun onReaderPageChanged(page: Int) {
        var activity = activity
        if(activity != null && activity is ReaderCallback) {
            activity.onReaderPageChanged(page)
        }
    }

    override fun getCurrentPage(): Int {
        var activity = activity
        if(activity != null && activity is ReaderCallback) {
            return activity.getCurrentPage()
        }
        return 1
    }

    override fun onPrevChapter() {
        var activity = activity
        if(activity != null && activity is ReaderCallback) {
            activity.onPrevChapter()
        }
    }

    override fun onNextChapter() {
        var activity = activity
        if(activity != null && activity is ReaderCallback) {
            activity.onNextChapter()
        }
    }

    override fun onLoadPrevChapter() {
        var activity = activity
        if(activity != null && activity is ReaderCallback) {
            activity.onLoadPrevChapter()
        }
    }

    override fun onLoadNextChapter() {
        var activity = activity
        if(activity != null && activity is ReaderCallback) {
            activity.onLoadNextChapter()
        }
    }

    fun addAll(collection: List<ImageUrl>) {
        mAdapter.addAll(collection)
    }

    fun addAll(location: Int, collection: List<ImageUrl>) {
        mAdapter.addAll(location, collection)
    }

    abstract fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean)

    abstract fun prevPage()

    abstract fun nextPage()

    abstract fun scrollToPosition(page: Int)

    abstract fun getCurrentItemPosition(): Int
}