package top.rechinx.meow.module.detail

import android.content.Context
import android.content.Intent
import android.graphics.Rect
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import butterknife.BindView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import top.rechinx.meow.R
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseActivity
import top.rechinx.meow.module.reader.ReaderActivity
import top.rechinx.meow.module.base.BaseAdapter
import top.rechinx.meow.support.log.L

class DetailActivity : BaseActivity(), DetailView, BaseAdapter.OnItemClickListener, DetailAdapter.OnClickCallback {

    @BindView(R.id.coordinator_recycler_view) lateinit var mRecyclerView: RecyclerView
    @BindView(R.id.coordinator_layout) lateinit var mLayoutView: CoordinatorLayout
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar
    @BindView(R.id.coordinator_refresh_layout) lateinit var mRefreshLayout: SmartRefreshLayout

    private lateinit var mAdapter: DetailAdapter
    private lateinit var mPresenter: DetailPresenter
    private lateinit var mComic: Comic

    override fun initData() {
        val source = intent.getStringExtra(EXTRA_SOURCE)
        val cid = intent.getStringExtra(EXTRA_CID)
        mComic = ComicManager.getInstance().identify(source, cid)
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
        mAdapter.setOnClickCallback(this)
        mAdapter.setOnFavoriteClickCallback(this)
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

    override fun initToolbar() {
        super.initToolbar()
        mToolbar?.setNavigationOnClickListener { finish() }
    }

    override fun onComicLoadSuccess(comic: Comic) {
        mPresenter.updateComic()
        mAdapter.setComic(comic)
//        val resId = if (comic.favorite != null) R.drawable.ic_favorite_white_24dp else R.drawable.ic_favorite_border_white_24dp
////        mActionButton.setImageResource(resId)
////        mActionButton.visibility = View.VISIBLE
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
            val intent = ReaderActivity.createIntent(this, mComic.source!!, mAdapter.getComic()?.cid!!, chapter.chapter_id!!, 1, mAdapter.getDataSet(), mAdapter.getReaderMode())
            startActivity(intent)
        }
    }

    override fun onClick(view: View, type: Int) {
        if(type == 1) {
            val intent = if(mComic.last_chapter == null) {
                ReaderActivity.createIntent(this, mComic.source!!, mAdapter.getComic()?.cid!!, mAdapter.getFirst().chapter_id!!, 1, mAdapter.getDataSet(), mAdapter.getReaderMode())
            }else {
                ReaderActivity.createIntent(this, mComic.source!!, mAdapter.getComic()?.cid!!, mComic.last_chapter!!, mComic.last_page!!, mAdapter.getDataSet(), mAdapter.getReaderMode())
            }
            (view as TextView).text = getString(R.string.details_continue)
            startActivity(intent)
        } else {
            if(mPresenter.mComic?.favorite != null) {
                mPresenter.unFavoriteComic()
                (view as TextView).text = getString(R.string.details_favorite)
                //mActionButton.setImageResource(R.drawable.ic_favorite_border_white_24dp)
            }else {
                mPresenter.favoriteComic()
                (view as TextView).text = getString(R.string.details_unfavorite)
                //mActionButton.setImageResource(R.drawable.ic_favorite_white_24dp)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        mComic = ComicManager.getInstance().identify(mComic.source!!, mComic.cid!!)
    }

    companion object {

        private val EXTRA_CID = "extra_keyword"
        private val EXTRA_SOURCE = "extra_source"

        fun createIntent(context: Context, source: String, cid: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, source)
            intent.putExtra(EXTRA_CID, cid)
            return intent
        }
    }

}
