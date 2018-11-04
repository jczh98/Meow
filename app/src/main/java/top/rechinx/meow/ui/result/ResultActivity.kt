package top.rechinx.meow.ui.result

import androidx.lifecycle.Observer
import android.content.Context
import android.content.Intent
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.details.DetailActivity

class ResultActivity: BaseActivity(), ResultContract.View, BaseAdapter.OnItemClickListener {

    override val presenter: ResultContract.Presenter by inject()

    private val resultList by bindView<RecyclerView>(R.id.result_recycler_view)
    private val progressBar by bindView<ProgressBar>(R.id.custom_progress_bar)
    private val layoutView by bindView<FrameLayout>(R.id.result_layout)
    private val refreshLayout by bindView<SmartRefreshLayout>(R.id.result_refresh_layout)

    private val layoutManager = LinearLayoutManager(this)
    private val keyword: String by lazy { intent.getStringExtra(Extras.EXTRA_KEYWORD) }

    private lateinit var resultAdapter: ResultAdapter

    override fun initViews() {
        resultAdapter = ResultAdapter(this, ArrayList())
        resultAdapter.setOnItemClickListener(this)
        resultList.layoutManager = layoutManager
        resultList.adapter = resultAdapter
        refreshLayout.setRefreshHeader(MaterialHeader(this))
        refreshLayout.setRefreshFooter(ClassicsFooter(this))
        refreshLayout.setOnLoadMoreListener {
            presenter.search(keyword, true)
        }
        refreshLayout.setOnRefreshListener {
            resultAdapter.clear()
            presenter.refresh(keyword)
        }

    }

    override fun initData() {
        presenter.search(keyword)
    }

    override fun onStart() {
        super.onStart()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }


    override fun getLayoutId(): Int = R.layout.activity_result

    override fun initToolbar() {
        super.initToolbar()
        toolbar?.setNavigationOnClickListener { finish() }
        supportActionBar?.title = String.format(getString(R.string.search_result_title), keyword)
    }

    private fun hideProgressBar() {
        if (progressBar != null) {
            progressBar.visibility = View.GONE
        }
    }

    override fun onMangaLoadCompleted(manga: Manga) {
        hideProgressBar()
        refreshLayout.finishRefresh()
        resultAdapter.add(manga)
    }

    override fun onLoadError() {
        hideProgressBar()
        Snackbar.make(layoutView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT).show()
        refreshLayout.finishLoadMore(1000,false, true)
        refreshLayout.finishRefresh()
    }

    override fun onLoadMoreCompleted() {
        refreshLayout.finishLoadMore(500)
    }

    override fun onItemClick(view: View, position: Int) {
        val manga = resultAdapter.getItem(position)
        val intent = DetailActivity.createIntent(this, manga.sourceId, manga.cid!!)
        startActivity(intent)
    }

    companion object {

        fun createIntent(context: Context, keyword: String): Intent {
            val intent = Intent(context, ResultActivity::class.java)
            intent.putExtra(Extras.EXTRA_KEYWORD, keyword)
            return intent
        }

    }
}