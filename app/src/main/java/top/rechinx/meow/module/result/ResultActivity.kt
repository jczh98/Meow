package top.rechinx.meow.module.result

import android.content.Context
import android.content.Intent
import android.graphics.PorterDuff
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import top.rechinx.meow.R
import top.rechinx.meow.module.base.BaseActivity
import android.provider.ContactsContract.QuickContact.EXTRA_MODE
import android.support.design.widget.Snackbar
import android.support.v4.content.ContextCompat
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import butterknife.BindView
import top.rechinx.meow.model.Comic
import java.util.*
import kotlin.collections.ArrayList


class ResultActivity : BaseActivity(), ResultView {

    @BindView(R.id.result_recycler_view) lateinit var mResultList: RecyclerView
    @BindView(R.id.custom_progress_bar) lateinit var mProgressBar: ProgressBar
    @BindView(R.id.result_layout) lateinit var mLayoutView: FrameLayout

    private lateinit var mLayoutManager: LinearLayoutManager
    private lateinit var mResultPresenter: ResultPresenter
    private lateinit var mResultAdapter: ResultAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun getLayoutId(): Int  = R.layout.activity_result

    override fun initPresenter() {
        val keyword = intent.getStringExtra(EXTRA_KEYWORD)
        val sources = intent.getIntArrayExtra(EXTRA_SOURCE)
        mResultPresenter = ResultPresenter(sources, keyword)
        mResultPresenter.attachView(this)
    }

    override fun initToolbar() {
        super.initToolbar()
        mToolbar.setNavigationOnClickListener { finish() }
    }

    override fun initView() {
        mLayoutManager = LinearLayoutManager(this)
        mResultAdapter = ResultAdapter(this, ArrayList())
        mResultList.layoutManager = mLayoutManager
        mResultList.adapter = mResultAdapter
    }

    override fun initData() {
        mResultPresenter.loadSearch()
    }

    override fun onSearchError() {
        hideProgressBar()
        Snackbar.make(mLayoutView, "Result is empty", Snackbar.LENGTH_SHORT).show()
    }

    override fun onSearchSuccess(comic: Comic) {
        hideProgressBar()
        mResultAdapter.add(comic)
    }

    private fun hideProgressBar() {
        if (mProgressBar != null) {
            mProgressBar.visibility = View.GONE
        }
    }

    companion object {

        private val EXTRA_KEYWORD = "extra_keyword"
        private val EXTRA_SOURCE = "extra_source"

        fun createIntent(context: Context, keyword: String, array: IntArray): Intent {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(EXTRA_SOURCE, array)
            intent.putExtra(EXTRA_KEYWORD, keyword)
            return intent
        }

    }

}
