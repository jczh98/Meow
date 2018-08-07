package top.rechinx.meow.module.reader

import android.content.Context
import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.TextView
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.widget.ReverseSeekBar

class ReaderActivity : BaseActivity(), ReaderView {

    @BindView(R.id.reader_chapter_title) lateinit var mChapterTitle: TextView
    @BindView(R.id.reader_chapter_page) lateinit var mChapterPage: TextView
    @BindView(R.id.reader_battery) lateinit var mBatteryText: TextView
    @BindView(R.id.reader_progress_layout) lateinit var mProgressLayout: View
    @BindView(R.id.reader_back_layout) lateinit var mBackLayout: View
    @BindView(R.id.reader_info_layout) lateinit var mInfoLayout: View
    @BindView(R.id.reader_seek_bar) lateinit var mSeekBar: ReverseSeekBar
    @BindView(R.id.reader_loading) lateinit var mLoadingText: TextView
    @BindView(R.id.reader_recycler_view) lateinit var mRecyclerView: RecyclerView

    protected lateinit var mPresenter: ReaderPresenter
    protected lateinit var mAdapter: ReaderAdapter

    override fun initData() {
        val cid = intent.getStringExtra(EXTRA_ID)
        val list = intent.getParcelableArrayListExtra<Chapter>(EXTRA_CHAPTER)
        val chapterId = intent.getStringExtra(EXTRA_CHAPTER_ID)
        mPresenter.loadInit(cid, chapterId, list.toArray(arrayOfNulls<Chapter>(list.size)))
    }

    override fun initView() {
        mAdapter = ReaderAdapter(this, ArrayList())
        mRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        mRecyclerView.adapter = mAdapter
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

    companion object {

        private val EXTRA_ID = "extra_id"
        private val EXTRA_CHAPTER = "extra_chapter"
        private val EXTRA_CHAPTER_ID = "extra_chapter_id"

        fun createIntent(context: Context, cid: String, chapter_id: String, array: ArrayList<Chapter>): Intent {
            val intent = Intent(context, ReaderActivity::class.java)
            intent.putExtra(EXTRA_ID, cid)
            intent.putExtra(EXTRA_CHAPTER, array)
            intent.putExtra(EXTRA_CHAPTER_ID, chapter_id)
            return intent
        }

    }
}
