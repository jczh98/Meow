package top.rechinx.meow.module.reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.R
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.support.relog.ReLog
import top.rechinx.meow.support.rvp.RecyclerViewPager
import top.rechinx.meow.widget.ReverseSeekBar

class ReaderActivity : BaseActivity(), ReaderView, RecyclerViewPager.OnPageChangedListener,  ReaderAdapter.OnTouchCallback, DiscreteSeekBar.OnProgressChangeListener {

    @BindView(R.id.reader_chapter_title) lateinit var mChapterTitle: TextView
    @BindView(R.id.reader_chapter_page) lateinit var mChapterPage: TextView
    @BindView(R.id.reader_battery) lateinit var mBatteryText: TextView
    @BindView(R.id.reader_progress_layout) lateinit var mProgressLayout: View
    @BindView(R.id.reader_back_layout) lateinit var mBackLayout: View
    @BindView(R.id.reader_info_layout) lateinit var mInfoLayout: View
    @BindView(R.id.reader_seek_bar) lateinit var mSeekBar: ReverseSeekBar
    @BindView(R.id.reader_loading) lateinit var mLoadingText: TextView
    @BindView(R.id.reader_recycler_view) lateinit var mRecyclerView: RecyclerViewPager

    protected lateinit var mPresenter: ReaderPresenter
    protected lateinit var mAdapter: ReaderAdapter

    private var max: Int = 1
    private var currentPage: Int = 1

    override fun initData() {
        val source = intent.getIntExtra(EXTRA_SOURCE, 0)
        val cid = intent.getStringExtra(EXTRA_ID)
        val list = intent.getParcelableArrayListExtra<Chapter>(EXTRA_CHAPTER)
        val chapterId = intent.getStringExtra(EXTRA_CHAPTER_ID)
        currentPage = intent.getIntExtra(EXTRA_PAGE, 1)
        mPresenter.loadInit(source, cid, chapterId, list.toArray(arrayOfNulls<Chapter>(list.size)))
    }

    override fun initView() {
        // Hidden status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        mAdapter = ReaderAdapter(this, ArrayList())
        mAdapter.setOnTouchCallback(this)
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.adapter = mAdapter
        mRecyclerView.setTriggerOffset(0.01f * 10)
        mRecyclerView.setScrollSpeed(0.02f)
        mRecyclerView.itemAnimator = null
        mRecyclerView.setItemViewCacheSize(2)
        mRecyclerView.setOnPageChangedListener(this)
        // SeekBar listener
        mSeekBar.setOnProgressChangeListener(this)
        // Back button

    }

    override fun getLayoutId(): Int = R.layout.activity_reader

    override fun initPresenter() {
        mPresenter = ReaderPresenter()
        mPresenter.attachView(this)
    }

    override fun onInitLoadSuccess(it: List<ImageUrl>) {
        mAdapter.addAll(it)
        mLoadingText.visibility = View.GONE
        mRecyclerView.visibility = View.VISIBLE
        mRecyclerView.scrollToPosition(currentPage - 1)
        updateProgress()
    }

    override fun onPrevLoadSuccess(it: List<ImageUrl>) {
        mAdapter.addAll(0, it)
    }

    override fun onNextLoadSuccess(it: List<ImageUrl>) {
        mAdapter.addAll(it)
    }

    override fun onParseError() {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onPrevLoading() {
        Toast.makeText(this, "正在加载上一话", Toast.LENGTH_SHORT).show()
    }

    override fun onNextLoading() {
        Toast.makeText(this, "正在加载下一话", Toast.LENGTH_SHORT).show()
    }

    override fun onPrevLoadNone() {
        Toast.makeText(this, "没有上一话了", Toast.LENGTH_SHORT).show()
    }

    override fun onNextLoadNone() {
        Toast.makeText(this, "已经是最新一话", Toast.LENGTH_SHORT).show()
    }

    override fun onChapterChanged(chapter: Chapter) {
        max = chapter.count
        mChapterTitle.text = chapter.title
        mSeekBar.max = max
    }

    private fun updateProgress() {
        mChapterPage.text = "$currentPage/$max"
        mSeekBar.progress = currentPage
    }
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

    override fun onPause() {
        super.onPause()
        mPresenter.updateLast(currentPage)
        unregisterReceiver(batteryReceiver)
    }

    override fun onDestroy() {
        super.onDestroy()
        mPresenter.updateLast(currentPage)
    }

    override fun onResume() {
        super.onResume()
        registerReceiver(batteryReceiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
    }

    /**
     * Battery broadcast receiver
     */
    private val batteryReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            if (Intent.ACTION_BATTERY_CHANGED == intent.action) {
                val level = intent.getIntExtra("level", 0)
                val scale = intent.getIntExtra("scale", 100)
                val text = (level * 100 / scale).toString() + "%"
                mBatteryText.text = text
            }
        }
    }


    /**
     * PhotoView touch event
     */

    override fun onCenter() {
        ReLog.d("Center")
        switchControl()
    }

    override fun onPrev() {
        ReLog.d("Prev")
        val cur = mRecyclerView.currentPosition
        if(cur - 1 >= 0) mRecyclerView.smoothScrollToPosition(cur - 1)
    }

    override fun onNext() {
        ReLog.d("Next")
        val cur = mRecyclerView.currentPosition
        if(cur + 1 < mAdapter.itemCount) mRecyclerView.smoothScrollToPosition(cur + 1)
    }

    fun switchControl() {
        if(mProgressLayout.isShown) {
            mBackLayout.visibility = View.INVISIBLE
            mProgressLayout.visibility = View.INVISIBLE
            mInfoLayout.visibility = View.VISIBLE
        }else {
            mBackLayout.visibility = View.VISIBLE
            mProgressLayout.visibility = View.VISIBLE
            mInfoLayout.visibility = View.INVISIBLE
        }
    }

    /**
     * SeekBar progress changed
     */
    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        if(fromUser) {
            val current = mRecyclerView.currentPosition + value - currentPage
            val pos = mAdapter.getPositionByNum(current, value, value < currentPage)
            mRecyclerView.scrollToPosition(pos)
        }
    }

    override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
    }


    @OnClick(R.id.reader_back_btn) fun onBackClick() {finish()}

    companion object {

        private val EXTRA_ID = "extra_id"
        private val EXTRA_CHAPTER = "extra_chapter"
        private val EXTRA_CHAPTER_ID = "extra_chapter_id"
        private val EXTRA_SOURCE = "extra_source"
        private val EXTRA_PAGE = "extra_page"

        fun createIntent(context: Context, source: Int, cid: String, chapter_id: String, page: Int, array: ArrayList<Chapter>): Intent {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, source)
            intent.putExtra(EXTRA_ID, cid)
            intent.putExtra(EXTRA_CHAPTER, array)
            intent.putExtra(EXTRA_CHAPTER_ID, chapter_id)
            intent.putExtra(EXTRA_PAGE, page)
            return intent
        }

    }
}
