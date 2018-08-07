package top.rechinx.meow.module.detail

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.FloatingActionButton
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import butterknife.BindView
import top.rechinx.meow.R
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.reader.ReaderActivity

class DetailActivity : BaseActivity(), DetailView, DetailAdapter.OnItemClickListener {

    @BindView(R.id.coordinator_action_button) lateinit var mActionButton: FloatingActionButton
    @BindView(R.id.coordinator_recycler_view) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.coordinator_layout) lateinit var mLayoutView: CoordinatorLayout
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar

    private lateinit var mAdapter: DetailAdapter
    private lateinit var mPresenter: DetailPresenter

    override fun initData() {
        val source = intent.getIntExtra(EXTRA_SOURCE, -1)
        val cid = intent.getStringExtra(EXTRA_CID)
        mPresenter.load(source, cid)
    }

    override fun initView() {
        mRecyclerView.layoutManager = GridLayoutManager(this, 4)
        mRecyclerView.setHasFixedSize(true)
        mRecyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                val position = parent.getChildLayoutPosition(view)
                if (position == 0) {
                    outRect.set(0, 0, 0, 10)
                } else {
                    val offset = parent.width / 40
                    outRect.set(offset, 0, offset, (offset * 1.5).toInt())
                }
            }
        })
        mAdapter = DetailAdapter(this, ArrayList())
        mAdapter.setOnItemClickListener(this)
        mRecyclerView.adapter = mAdapter
    }

    private fun hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.visibility = View.GONE
        }
    }

    override fun getLayoutId(): Int = R.layout.activity_detail

    override fun initPresenter() {
        mPresenter = DetailPresenter()
        mPresenter.attachView(this)
    }

    override fun onComicLoadSuccess(comic: Comic) {
        mAdapter.setComic(comic)
    }

    override fun onChapterLoadSuccess(list: List<Chapter>) {
        hideProgressBar()
        mAdapter.addAll(list)
    }

    override fun onParseError() {
        hideProgressBar()
        Snackbar.make(mLayoutView, "error", Snackbar.LENGTH_SHORT).show()
    }

    override fun onItemClick(view: View, position: Int) {
        if(position != 0) {
            val chapter = mAdapter.getItem(position - 1)
            val intent = ReaderActivity.createIntent(this, mAdapter.getComic()?.cid!!, chapter.chapter_id!!, mAdapter.getDataSet())
            startActivity(intent)
        }
    }

    companion object {

        private val EXTRA_CID = "extra_keyword"
        private val EXTRA_SOURCE = "extra_source"

        fun createIntent(context: Context, source: Int, cid: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, source)
            intent.putExtra(EXTRA_CID, cid)
            return intent
        }
    }

}
