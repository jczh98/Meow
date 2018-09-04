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
import butterknife.OnClick
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.api.RefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import top.rechinx.meow.R
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.reader.ReaderActivity
import top.rechinx.meow.support.relog.ReLog

class DetailActivity : BaseActivity(), DetailView, DetailAdapter.OnItemClickListener {

    @BindView(R.id.coordinator_action_button) lateinit var mActionButton: FloatingActionButton
    @BindView(R.id.coordinator_recycler_view) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.coordinator_layout) lateinit var mLayoutView: CoordinatorLayout
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar
    @BindView(R.id.coordinator_refresh_layout) lateinit var mRefreshLayout: SmartRefreshLayout

    private lateinit var mAdapter: DetailAdapter
    private lateinit var mPresenter: DetailPresenter
    private lateinit var mComic: Comic

    override fun initData() {
        val source = intent.getIntExtra(EXTRA_SOURCE, -1)
        val cid = intent.getStringExtra(EXTRA_CID)
        mComic = Comic(source, cid)
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
        // Refresh layout setup
        mRefreshLayout.setRefreshFooter(ClassicsFooter(this))
        mRefreshLayout.setOnLoadMoreListener {
            mPresenter.loadMore()
        }
        mRefreshLayout.setOnRefreshListener {
            mAdapter.clearAll()
            mPresenter.refresh()
        }
    }

    override fun onLoadMoreSuccess(list: List<Chapter>) {
        mAdapter.addAll(list)
        mRefreshLayout.finishLoadMore(500)
    }

    override fun onLoadMoreFailure() {
        mRefreshLayout.finishLoadMore(1000,false, true)
    }

    override fun onRefreshFinished() {
        mRefreshLayout.finishRefresh()
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
        val resId = if (comic.favorite != null) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_border_white_24dp
        mActionButton.setImageResource(resId)
        mActionButton.visibility = View.VISIBLE
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
            val intent = ReaderActivity.createIntent(this, mComic.source!!, mAdapter.getComic()?.cid!!, chapter.chapter_id!!, mAdapter.getDataSet())
            startActivity(intent)
        }
    }

    @OnClick(R.id.coordinator_action_button)
    fun onActionButtonClick() {
        if(mPresenter.mComic?.favorite != null) {
            mPresenter.unFavoriteComic()
            mActionButton.setImageResource(R.drawable.ic_favorite_border_white_24dp)
        }else {
            mPresenter.favoriteComic()
            mActionButton.setImageResource(R.drawable.ic_favorite_white_24dp)
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
