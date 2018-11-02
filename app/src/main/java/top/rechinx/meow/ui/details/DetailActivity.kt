package top.rechinx.meow.ui.details

import android.arch.lifecycle.Observer
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
import com.scwang.smartrefresh.header.MaterialHeader
import com.scwang.smartrefresh.layout.SmartRefreshLayout
import com.scwang.smartrefresh.layout.footer.BallPulseFooter
import com.scwang.smartrefresh.layout.footer.ClassicsFooter
import com.scwang.smartrefresh.layout.footer.FalsifyFooter
import org.koin.android.ext.android.inject
import org.koin.android.viewmodel.ext.android.viewModel
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.reader.ReaderActivity

class DetailActivity: BaseActivity(), DetailContract.View, BaseAdapter.OnItemClickListener, DetailAdapter.OnClickCallback {

    override val presenter: DetailContract.Presenter by inject()

    private val recyclerView: RecyclerView by bindView(R.id.coordinator_recycler_view)
    private val layoutView: CoordinatorLayout by bindView(R.id.coordinator_layout)
    private val progressBar: ProgressBar by bindView(R.id.custom_progress_bar)
    private val refreshLayout: SmartRefreshLayout by bindView(R.id.coordinator_refresh_layout)

    val sourceId: Long by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }
    val cid: String by lazy { intent.getStringExtra(Extras.EXTRA_CID) }

    private lateinit var adapter: DetailAdapter
    private var manga: Manga? = null

    override fun onStart() {
        super.onStart()
        presenter.subscribe(this)
    }
    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun initViews() {
        toolbar?.setNavigationOnClickListener { finish() }
        recyclerView.layoutManager = GridLayoutManager(this, 4)
        recyclerView.setHasFixedSize(true)
        recyclerView.itemAnimator = null
        recyclerView.addItemDecoration(object : RecyclerView.ItemDecoration() {
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
        adapter = DetailAdapter(this, ArrayList())
        adapter.setOnItemClickListener(this)
        adapter.setOnClickCallback(this)
        adapter.setOnFavoriteClickCallback(this)
        recyclerView.adapter = adapter
        // Refresh layout setup
        refreshLayout.setRefreshHeader(MaterialHeader(this))
        refreshLayout.setRefreshFooter(FalsifyFooter(this))
        refreshLayout.setOnRefreshListener {
            adapter.clear()
            presenter.fetchMangaInfo(sourceId, cid)
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.fetchMangaInfo(sourceId, cid)
    }

    private fun hideProgressBar() {
        if (progressBar != null) {
            progressBar.visibility = View.GONE
        }
    }

    override fun onMangaLoadCompleted(manga: Manga) {
        finishRefreshLayout()
        this.manga = manga
        adapter.manga = manga
        presenter.fetchMangaChapters(sourceId, cid)
    }

    override fun onMangaFetchError() {
        finishRefreshLayout()
        hideProgressBar()
        Snackbar.make(layoutView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT).show()
    }

    override fun onChaptersInit(chapters: List<Chapter>) {
        finishRefreshLayout()
        hideProgressBar()
        adapter.addAll(chapters)
    }

    override fun onChaptersFetchError() {
        finishRefreshLayout()
        Snackbar.make(layoutView, getString(R.string.snackbar_result_empty), Snackbar.LENGTH_SHORT).show()
    }

    private fun finishRefreshLayout() {
        refreshLayout.finishRefresh()
    }

    override fun onClick(view: View, type: Int) {
        val manga = manga!!// must exist
        if(type == 1) {
            if(manga.last_read_chapter_id != -1L) {
                val chapter = adapter.datas.first { manga.last_read_chapter_id == it.id }
                startReader(chapter, true)
            } else {
                startReader(-1, false)
            }
            (view as TextView).text = getString(R.string.details_continue)
        } else {
            presenter.favoriteOrNot(manga)
            (view as TextView).text = if (manga.favorite) getString(R.string.details_unfavorite) else getString(R.string.details_favorite)
            manga.favorite = !manga.favorite
        }
    }

    override fun onItemClick(view: View, position: Int) {
        if(position != 0) {
            startReader(position)
        }
    }

    private fun startReader(position: Int, isContinued: Boolean = false) {
        if(position == -1) {
            val chapter = adapter.getItem(adapter.itemCount - 2)
            startReader(chapter)
        } else {
            val chapter = adapter.getItem(position - 1)
            startReader(chapter)
        }
    }

    private fun startReader(chapter: Chapter, isContinued: Boolean = false) {
        presenter.markedAsHistory(adapter.manga!!)
        val intent = ReaderActivity.createIntent(this, adapter.manga!!, chapter, isContinued)
        startActivity(intent)
    }

    override fun getLayoutId(): Int = R.layout.activity_detail

    companion object {

        fun createIntent(context: Context, source: Long, cid: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Extras.EXTRA_SOURCE, source)
            intent.putExtra(Extras.EXTRA_CID, cid)
            return intent
        }
    }
}