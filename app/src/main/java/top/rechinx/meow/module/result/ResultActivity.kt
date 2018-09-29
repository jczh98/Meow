package top.rechinx.meow.module.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import top.rechinx.meow.R
import top.rechinx.meow.module.base.BaseActivity
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import butterknife.BindView
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseAdapter
import top.rechinx.meow.module.detail.DetailActivity
import kotlin.collections.ArrayList


class ResultActivity : BaseActivity(), ResultView, BaseAdapter.OnItemClickListener {

    @BindView(R.id.result_recycler_view) lateinit var mResultList: RecyclerView
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar
    @BindView(R.id.result_layout) lateinit var mLayoutView: FrameLayout
    @BindView(R.id.result_refresh_layout) lateinit var mRefreshLayout: SmartRefreshLayout

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mResultPresenter: ResultPresenter
    private lateinit var mResultAdapter: ResultAdapter
    private lateinit var keyword: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int  = R.layout.activity_result

    override fun initPresenter() {
        keyword = intent.getStringExtra(EXTRA_KEYWORD)
        mResultPresenter = ResultPresenter(keyword)
        mResultPresenter.attachView(this)
    }

    override fun initToolbar() {
        super.initToolbar()
        mToolbar?.setNavigationOnClickListener { finish() }
        supportActionBar?.title = String.format(getString(R.string.search_result_title), keyword)
    }

    override fun initView() {
        mLayoutManager = LinearLayoutManager(this)
        mResultAdapter = ResultAdapter(this, ArrayList())
        mResultAdapter.setOnItemClickListener(this)
        mResultList.layoutManager = mLayoutManager
        mResultList.adapter = mResultAdapter
        // RefreshLayout setup
        mRefreshLayout.setRefreshFooter(ClassicsFooter(this))
        mRefreshLayout.setOnLoadMoreListener {
            mResultPresenter.loadSearch(true)
        }
        mRefreshLayout.setOnRefreshListener {
            mResultAdapter.clear()
            mResultPresenter.loadRefresh()
        }
    }

    override fun onItemClick(view: View, position: Int) {
        val comic = mResultAdapter.getItem(position)
        val intent = DetailActivity.createIntent(this, comic.source!!, comic.cid!!)
        startActivity(intent)
    }

    override fun initData() {
        mResultPresenter.loadSearch(false)
    }

    override fun onSearchError() {
        hideProgressBar()
        Snackbar.make(mLayoutView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT).show()
        mRefreshLayout.finishLoadMore(1000,false, true)
    }

    override fun onSearchSuccess(comic: Comic) {
        hideProgressBar()
        mRefreshLayout.finishRefresh()
        mResultAdapter.add(comic)
    }

    override fun onLoadMoreSuccess() {
        mRefreshLayout.finishLoadMore(500)
    }

    override fun onLoadMoreFailure() {
        mRefreshLayout.finishLoadMore(500)
    }

    private fun hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.visibility = View.GONE
        }
    }

    companion object {

        const val EXTRA_KEYWORD = "extra_keyword"
        const val EXTRA_SOURCE = "extra_source"

        fun createIntent(context: Context, keyword: String): Intent {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(EXTRA_KEYWORD, keyword)
            return intent
        }

    }

}
