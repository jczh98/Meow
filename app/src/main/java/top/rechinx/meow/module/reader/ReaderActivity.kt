package top.rechinx.meow.module.reader

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Point
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.WindowManager
import android.widget.FrameLayout
import android.widget.TextView
import android.widget.Toast
import butterknife.BindView
import butterknife.OnClick
import com.github.chrisbanes.photoview.OnViewTapListener
import org.adw.library.widgets.discreteseekbar.DiscreteSeekBar
import top.rechinx.meow.Constants
import top.rechinx.meow.R
import top.rechinx.meow.manager.PreferenceManager
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.support.log.L
import top.rechinx.meow.widget.ReverseSeekBar

class ReaderActivity : BaseActivity(), ReaderCallback, ReaderView, DiscreteSeekBar.OnProgressChangeListener, OnViewTapListener {

    @BindView(R.id.reader_chapter_title) lateinit var mChapterTitle: TextView
    @BindView(R.id.reader_chapter_page) lateinit var mChapterPage: TextView
    @BindView(R.id.reader_battery) lateinit var mBatteryText: TextView
    @BindView(R.id.reader_progress_layout) lateinit var mProgressLayout: View
    @BindView(R.id.reader_back_layout) lateinit var mBackLayout: View
    @BindView(R.id.reader_info_layout) lateinit var mInfoLayout: View
    @BindView(R.id.reader_seek_bar) lateinit var mSeekBar: ReverseSeekBar
    @BindView(R.id.reader_loading) lateinit var mLoadingText: TextView
    //@BindView(R.id.reader_recycler_view) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.reader_content) lateinit var mFrame: FrameLayout

    private lateinit var mPreferenceManager: PreferenceManager
    private lateinit var mPresenter: ReaderPresenter
    //private lateinit var mAdapter: ReaderAdapter
    private var mReader: ReaderFragment? = null

    private var max: Int = 1
    private var currentPage: Int = 1
    private var sMode: Int = 0

    override fun initData() {
        val source = intent.getStringExtra(EXTRA_SOURCE)
        val cid = intent.getStringExtra(EXTRA_ID)
        val list = intent.getParcelableArrayListExtra<Chapter>(EXTRA_CHAPTER)
        val chapterId = intent.getStringExtra(EXTRA_CHAPTER_ID)
        sMode = intent.getIntExtra(EXTRA_MODE, 0)
        currentPage = intent.getIntExtra(EXTRA_PAGE, 1)
        mPresenter.loadInit(source, cid, chapterId, list.toArray(arrayOfNulls<Chapter>(list.size)))
    }

    override fun initView() {
        mPreferenceManager = PreferenceManager(this)
        // Hidden status bar
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        // init reader fragment
        setupReader(sMode)

        // Recycler view
//        mAdapter = ReaderAdapter(this, ArrayList())
//        mAdapter.setOnViewTapListener(this)
        //mAdapter.setOnTouchCallback(this)
//        mRecyclerView.layoutManager = getLayoutManager()
//        mRecyclerView.adapter = mAdapter
//        mRecyclerView.itemAnimator = null
//        mRecyclerView.setItemViewCacheSize(2)
        // SeekBar listener
        mSeekBar.setOnProgressChangeListener(this)
        // Hidden info
        if(mPreferenceManager.getBoolean(Constants.PREF_HIDE_READER_INFO, false)) {
            mInfoLayout.visibility = View.GONE
        }
    }

    private fun setupReader(mode: Int) {
        var saveStateBundle: Bundle? = null
        if(mReader != null) {
            saveStateBundle = Bundle()
            mReader?.onSaveInstanceState(saveStateBundle)
        }
        when(mode) {
            0 -> {
                mReader = PageReaderFragment()
            }
            1 -> {
                mReader = StreamReaderFragment()
            }
        }
        mReader?.arguments = saveStateBundle
        supportFragmentManager.beginTransaction()
                .replace(R.id.reader_content, mReader)
                .commit()
    }

    override fun initPresenter() {
        mPresenter = ReaderPresenter()
        mPresenter.attachView(this)
    }

    override fun onInitLoadSuccess(it: List<ImageUrl>) {
        mReader?.addAll(it)
        mLoadingText.visibility = View.GONE
        mFrame.visibility = View.VISIBLE
        //mRecyclerView.visibility = View.VISIBLE
        mReader?.scrollToPosition(currentPage - 1)
        mSeekBar.progress = currentPage
        updateProgress()
    }

    override fun onPrevLoadSuccess(it: List<ImageUrl>) {
        mReader?.addAll(0, it)
        //mAdapter.addAll(0, it)
    }

    override fun onNextLoadSuccess(it: List<ImageUrl>) {
        mReader?.addAll(it)
       // mAdapter.addAll(it)
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

    override fun onViewTap(view: View?, x: Float, y: Float) {
        val point = Point()
        windowManager.defaultDisplay.getSize(point)
        val limitX = point.x / 3.0f
        val limitY = point.y / 3.0f
        if(x < limitX) {
            mReader?.prevPage()
        } else if(x > 2 * limitX) {
            mReader?.nextPage()
        } else if(y < limitY) {
            mReader?.prevPage()
        } else if(y > 2 * limitY) {
            mReader?.nextPage()
        } else {
            switchControl()
        }
    }

    override fun onPrevChapter() {
        mPresenter.toPrevChapter()
    }

    override fun onNextChapter() {
        mPresenter.toNextChapter()
    }

    override fun onLoadPrevChapter() {
        mPresenter.loadPrev()
    }

    override fun onLoadNextChapter() {
        mPresenter.loadNext()
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


    fun switchControl() {
        if(mProgressLayout.isShown) {
            mBackLayout.visibility = View.INVISIBLE
            mProgressLayout.visibility = View.INVISIBLE
            if(!mPreferenceManager.getBoolean(Constants.PREF_HIDE_READER_INFO, false)) {
                mInfoLayout.visibility = View.VISIBLE
            }
        }else {
            mBackLayout.visibility = View.VISIBLE
            mProgressLayout.visibility = View.VISIBLE
            mInfoLayout.visibility = View.INVISIBLE
        }
    }


    override fun onStartTrackingTouch(seekBar: DiscreteSeekBar?) {
    }

    override fun onStopTrackingTouch(seekBar: DiscreteSeekBar?) {
    }


    override fun onProgressChanged(seekBar: DiscreteSeekBar?, value: Int, fromUser: Boolean) {
        mReader?.onProgressChanged(seekBar, value, fromUser)
    }

    override fun onReaderPageChanged(page: Int) {
        currentPage = page
        updateProgress()
    }

    override fun getCurrentPage(): Int = currentPage

    @OnClick(R.id.reader_back_btn) fun onBackClick() {finish()}

    @OnClick(R.id.reader_options) fun onOptionsClick() {
        sMode = sMode xor 1
        setupReader(sMode)
    }
    override fun getLayoutId(): Int = R.layout.activity_reader

    companion object {

        private const val EXTRA_ID = "extra_id"
        private const val EXTRA_CHAPTER = "extra_chapter"
        private const val EXTRA_CHAPTER_ID = "extra_chapter_id"
        private const val EXTRA_SOURCE = "extra_source"
        private const val EXTRA_PAGE = "extra_page"
        private const val EXTRA_MODE = "extra_mode"

        fun createIntent(context: Context, source: String, cid: String, chapter_id: String, page: Int, array: ArrayList<Chapter>, mode: Int): Intent {
            //val intent = getIntent(context, mode)
            val intent = Intent(context, ReaderActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, source)
            intent.putExtra(EXTRA_ID, cid)
            intent.putExtra(EXTRA_CHAPTER, array)
            intent.putExtra(EXTRA_CHAPTER_ID, chapter_id)
            intent.putExtra(EXTRA_PAGE, page)
            intent.putExtra(EXTRA_MODE, mode)
            return intent
        }

//        private fun getIntent(context: Context, mode: Int): Intent {
//            return if (mode == ReaderAdapter.PAGE_READER_MODE) {
//                Intent(context, PageReaderActivity::class.java)
//            } else {
//                Intent(context, StreamReaderActivity::class.java)
//            }
//        }

    }
}
