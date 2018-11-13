package top.rechinx.meow.ui.result

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.widget.FrameLayout
import android.widget.ProgressBar
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import kotlinx.android.synthetic.main.custom_toolbar.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.details.DetailActivity
import top.rechinx.rikka.mvp.MvpAppCompatActivityWithoutReflection

class ResultActivity: MvpAppCompatActivityWithoutReflection<ResultPresenter>(), BaseAdapter.OnItemClickListener {

    private val resultList by bindView<RecyclerView>(R.id.result_recycler_view)
    private val progressBar by bindView<ProgressBar>(R.id.custom_progress_bar)
    private val layoutView by bindView<FrameLayout>(R.id.result_layout)
    private val refreshLayout by bindView<SmartRefreshLayout>(R.id.result_refresh_layout)

    private val layoutManager = LinearLayoutManager(this)
    private val keyword: String by lazy { intent.getStringExtra(Extras.EXTRA_KEYWORD) }

    private lateinit var resultAdapter: ResultAdapter

    override fun createPresenter(): ResultPresenter {
        return ResultPresenter(keyword)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_result)
        if(custom_toolbar != null) {
            setSupportActionBar(custom_toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title = String.format(getString(R.string.search_result_title), keyword)
            custom_toolbar?.setNavigationOnClickListener { finish() }
        }
        initViews()
        presenter.search(false)
    }

    private fun initViews() {
        resultAdapter = ResultAdapter(this, ArrayList())
        resultAdapter.setOnItemClickListener(this)
        resultList.layoutManager = layoutManager
        resultList.adapter = resultAdapter
        refreshLayout.setRefreshHeader(MaterialHeader(this))
        refreshLayout.setRefreshFooter(ClassicsFooter(this))
        refreshLayout.setOnLoadMoreListener {
            presenter.search(true)
        }
        refreshLayout.setOnRefreshListener {
            resultAdapter.clear()
            presenter.refresh()
        }

    }


    private fun hideProgressBar() {
        if (progressBar != null) {
            progressBar.visibility = View.GONE
        }
    }

    fun onMangaLoadCompleted(manga: Manga) {
        hideProgressBar()
        refreshLayout.finishRefresh()
        refreshLayout.finishLoadMore()
        resultAdapter.add(manga)
    }

    fun onLoadError() {
        hideProgressBar()
        Snackbar.make(layoutView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT).show()
        refreshLayout.finishLoadMore(1000,false, true)
        refreshLayout.finishRefresh()
    }

    fun onLoadMoreCompleted() {
        refreshLayout.finishLoadMore(500)
    }

    override fun onItemClick(view: View, position: Int) {
        val manga = resultAdapter.getItem(position)
        val intent = DetailActivity.createIntent(this, manga.sourceId, manga.url!!)
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