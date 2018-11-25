package top.rechinx.meow.ui.details

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scwang.smartrefresh.header.MaterialHeader
import eu.davidea.flexibleadapter.FlexibleAdapter
import kotlinx.android.synthetic.main.activity_detail.*
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.SManga
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.download.DownloadService
import top.rechinx.meow.exception.NoMoreResultException
import top.rechinx.meow.global.Extras
import top.rechinx.meow.ui.details.chapters.ChaptersActivity
import top.rechinx.meow.ui.details.items.ChapterItem
import top.rechinx.meow.ui.details.items.HeaderItem
import top.rechinx.meow.ui.details.items.LoadItem
import top.rechinx.meow.ui.reader.ReaderActivity
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible
import top.rechinx.rikka.mvp.MvpAppCompatActivityWithoutReflection
import java.text.DateFormat
import java.util.*
import kotlin.collections.ArrayList

class DetailActivity: MvpAppCompatActivityWithoutReflection<DetailPresenter>(),
        FlexibleAdapter.OnItemClickListener, FlexibleAdapter.EndlessScrollListener {

    val sourceId: Long by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }
    val url: String by lazy { intent.getStringExtra(Extras.EXTRA_URL) }

    private var adapter: DetailAdapter? = null

    private var loadItem: LoadItem? = null

    private var headerItem: HeaderItem? = null

    private var toolbarMenu: Menu? = null

    override fun createPresenter(): DetailPresenter {
        return DetailPresenter(sourceId, url)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initViews()
        presenter.fetchMangaInfo(sourceId, url)
    }

    fun initViews() {
        if(toolbar != null) {
            setSupportActionBar(toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        supportActionBar?.title = getString(R.string.title_activity_detail)
        toolbar?.setNavigationOnClickListener { finish() }
        chaptersRecyclerView.setHasFixedSize(true)
        val layoutManager = GridLayoutManager(this, 4)
        layoutManager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (adapter?.getItem(position) is ChapterItem || adapter?.getItem(position) is LoadItem) 1 else 4
            }
        }
        chaptersRecyclerView.layoutManager = layoutManager
        adapter = DetailAdapter( this)
        adapter?.onLoadMoreListener = object : DetailAdapter.OnLoadMoreListener {
            override fun onLoadMore() {
                if(presenter.hasNextPage()) {
                    loadItem?.status = LoadItem.LOADING
                    presenter.requestNext()
                } else {
                    adapter?.onLoadMoreComplete(null)
                }
            }
        }
        chaptersRecyclerView.adapter = adapter
        fab.setOnClickListener {
            if(presenter.manga?.last_read_chapter_id != -1L) {
                for(index in 0 until adapter?.itemCount!!) {
                    val chapterItem = adapter?.getItem(index) as ChapterItem
                    if(chapterItem.chapter.id == presenter.manga?.last_read_chapter_id ) {
                        startReader(chapterItem.chapter, true)
                    }
                }
            } else {
                startReader(-1, false)
                (it as FloatingActionButton).setImageResource(R.drawable.ic_continue_read_white_24dp)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.bottombar_menu_with_unfavorite, menu)
        toolbarMenu = menu
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.action_favorite -> {
                if(presenter.manga != null) {
                    presenter.favoriteOrNot()
                    if(toolbarMenu != null) {
                        val item = toolbarMenu!!.findItem(R.id.action_favorite)
                        if (presenter.manga!!.favorite)  {
                            item.setIcon(R.drawable.ic_favorite_white_24dp)
                        } else {
                            item.setIcon(R.drawable.ic_favorite_border_white_24dp)
                        }
                    }
                }
            }
            R.id.action_download -> {
                val intent = ChaptersActivity.createIntent(this@DetailActivity, adapter!!.getChapters())
                startActivityForResult(intent, REQUEST_CODE_DOWNLOAD)
            }
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK) {
            when(requestCode) {
                REQUEST_CODE_DOWNLOAD -> {
                    val list = data?.getParcelableArrayListExtra<Chapter>(Extras.EXTRA_CHAPTERS)
                    presenter.addTask(adapter!!.getChapters(), list!!)
                }
            }
        }
    }

    fun onMangaLoadCompleted(manga: Manga) {
        setManga(manga)
        presenter.restartPager()
    }

    private fun setManga(manga: Manga) {
        presenter.manga = manga
        headerItem = HeaderItem(manga, this)
        Glide.with(this).load(manga).into(mangaBackCover)
        if(toolbarMenu != null) {
            val item = toolbarMenu!!.findItem(R.id.action_favorite)
            if (manga.favorite)  {
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_white_24dp)
            } else {
                item.icon = ContextCompat.getDrawable(this, R.drawable.ic_favorite_border_white_24dp)
            }
        }
        setFAB(manga)
    }

    private fun setMangaLastUpdated(manga: Manga) {
        adapter?.setMangaLastUpdated(manga)
    }

    private fun setFAB(manga: Manga) {
        fab.setImageResource(if(manga.last_read_chapter_id == -1L) R.drawable.ic_start_white_24dp else R.drawable.ic_continue_read_white_24dp)
    }

    fun onMangaFetchError() {
        showSnackbar(R.string.snackbar_result_empty)
    }


    private fun showSnackbar(resId: Int) {
        Snackbar.make(detailRootView, getString(resId), Snackbar.LENGTH_SHORT)
                .show()
    }


    override fun onItemClick(view: View, position: Int): Boolean {
        val adapter = adapter ?: return false
        if(adapter.getItem(position) !is ChapterItem) return false
        startReader(position)
        return true
    }

    private fun startReader(position: Int, isContinued: Boolean = false) {
        val adapter = adapter ?: return
        if(position == -1) {
            val chapterItem = adapter.getItem(adapter.itemCount - 1) as ChapterItem
            startReader(chapterItem.chapter)
        } else {
            val chapterItem = adapter.getItem(position) as ChapterItem
            startReader(chapterItem.chapter)
        }
    }

    private fun startReader(chapter: Chapter, isContinued: Boolean = false) {
        val manga = presenter.manga ?: return
        presenter.markedAsHistory(manga)
        val intent = ReaderActivity.createIntent(this, manga, chapter, isContinued)
        startActivity(intent)
    }


    fun onAddPage(page: Int, chapters: List<ChapterItem>) {
        val adapter = adapter ?: return
        val manga = presenter.manga ?: return
        loadItem?.status = LoadItem.IDLE
        manga.last_update = chapters.maxBy { it.chapter.date_updated }?.chapter?.date_updated ?: 0L
        if(page == 1) {
            adapter.clear()
            resetLoadItem()
            adapter.onLoadMoreComplete(listOf(headerItem) + chapters)
        } else {
            adapter.onLoadMoreComplete(chapters)
        }
        setMangaLastUpdated(manga)
    }

    private fun resetLoadItem() {
        loadItem = LoadItem()
        if(presenter.hasNextPage()) {
            adapter?.setEndlessProgressItem(loadItem)
        } else {
            adapter?.endlessTargetCount = 0
            adapter?.setEndlessScrollListener(this, loadItem!!)
        }

    }


    /**
     * empty functions for single page chapters without load more
     */
    override fun noMoreLoad(newItemsSize: Int) {}
    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        adapter?.onLoadMoreComplete(null)
        adapter?.endlessTargetCount = 1
    }

    fun onAddPageError(throwable: Throwable) {
        adapter?.onLoadMoreComplete(null)
        adapter?.endlessTargetCount = 1
        if(throwable is NoMoreResultException) {
            showSnackbar(R.string.snackbar_result_empty)
        } else {
            chaptersRecyclerView.gone()
            showSnackbar(R.string.snackbar_result_empty)
        }
    }

    fun setLastChanged(manga: Manga) {
        val adapter = adapter ?: return
        presenter.manga?.last_read_chapter_id = manga.last_read_chapter_id
        adapter.setLast(manga.last_read_chapter_id)
    }

    fun onTaskAddSuccess(list: ArrayList<Task>) {
        val intent = DownloadService.createIntent(this, list)
        startService(intent)
    }

    companion object {

        const val REQUEST_CODE_DOWNLOAD = 0

        fun createIntent(context: Context, source: Long, url: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Extras.EXTRA_SOURCE, source)
            intent.putExtra(Extras.EXTRA_URL, url)
            return intent
        }
    }
}