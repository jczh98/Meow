package top.rechinx.meow.ui.details

import android.content.Context
import android.content.Intent
import android.os.Bundle
import com.google.android.material.snackbar.Snackbar
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scwang.smartrefresh.header.MaterialHeader
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.activity_detail.*
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.SManga
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.exception.NoMoreResultException
import top.rechinx.meow.global.Extras
import top.rechinx.meow.ui.details.items.ChapterItem
import top.rechinx.meow.ui.filter.items.ProgressItem
import top.rechinx.meow.ui.reader.ReaderActivity
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible
import top.rechinx.rikka.mvp.MvpAppCompatActivityWithoutReflection
import java.text.DateFormat
import java.util.*

class DetailActivity: MvpAppCompatActivityWithoutReflection<DetailPresenter>(),
        FlexibleAdapter.EndlessScrollListener,
        FlexibleAdapter.OnItemClickListener{

    val sourceId: Long by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }
    val cid: String by lazy { intent.getStringExtra(Extras.EXTRA_CID) }

    private var adapter: DetailAdapter? = null

    private var progressItem: ProgressItem? = null

    override fun createPresenter(): DetailPresenter {
        return DetailPresenter(sourceId, cid)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)
        initViews()
        presenter.fetchMangaInfo(sourceId, cid)
    }

    fun initViews() {
        if(Toolbar != null) {
            setSupportActionBar(Toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        Toolbar?.setNavigationOnClickListener { finish() }
        chaptersRecyclerView.setHasFixedSize(false)
        adapter = DetailAdapter( this)
        chaptersRecyclerView.adapter = adapter
        // Refresh layout setup
        detailRefreshLayout.setRefreshHeader(MaterialHeader(this))
        detailRefreshLayout.setOnRefreshListener {
            chaptersProgressBar.visible()
            chaptersRecyclerView.gone()
            adapter?.clear()
            presenter.restartPager()
        }
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
        bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_favorite -> {
                    if(presenter.manga != null) {
                        presenter.favoriteOrNot()
                        if (presenter.manga!!.favorite)  {
                            bottomAppBar.replaceMenu(R.menu.bottombar_menu_with_unfavorite)
                        } else {
                            bottomAppBar.replaceMenu(R.menu.bottombar_menu_with_favorite)
                        }
                        presenter.manga!!.favorite = !presenter.manga!!.favorite
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        //presenter.fetchMangaInfo(sourceId, cid)
    }

    private fun hideProgressBar() {
        chaptersProgressBar.gone()
        chaptersRecyclerView.visible()
    }

    fun onMangaLoadCompleted(manga: Manga) {
        finishRefreshLayout()
        setManga(manga)
        presenter.restartPager()
    }

    private fun setManga(manga: Manga) {
        mangaInfoTitle.text = manga.title
        Glide.with(this).load(manga).into(mangaInfoCover)
        Glide.with(this).load(manga).into(mangaBackCover)
        mangaInfoAuthor.text = manga.author
        var statusString = ""
        when(manga.status) {
            SManga.ONGOING -> statusString += getString(R.string.string_manga_statu_ongoing)
            SManga.COMPLETED -> statusString += getString(R.string.string_manga_statu_completed)
            SManga.UNKNOWN -> statusString += getString(R.string.string_manga_statu_unknown)
        }
        mangaInfoStatus.text = statusString
        mangaInfoDescription.text = manga.description
        mangaGenresTags.setTags(manga.genre?.split(", "))
        // set favorite
        if (manga.favorite)  {
            bottomAppBar.replaceMenu(R.menu.bottombar_menu_with_unfavorite)
        } else {
            bottomAppBar.replaceMenu(R.menu.bottombar_menu_with_favorite)
        }
        setFAB(manga)
    }

    private fun setMangaLastUpdated(manga: Manga) {
        var updateLabel = ""
        updateLabel += if(manga.last_update != 0L) {
            DateFormat.getDateInstance(DateFormat.SHORT).format(Date(manga.last_update))
        } else {
            getString(R.string.unknown)
        }
        mangaInfoUpdated.text = updateLabel
    }

    private fun setFAB(manga: Manga) {
        fab.setImageResource(if(manga.last_read_chapter_id == -1L) R.drawable.ic_start_white_24dp else R.drawable.ic_continue_read_white_24dp)
    }

    fun onMangaFetchError() {
        finishRefreshLayout()
        hideProgressBar()
        showSnackbar(R.string.snackbar_result_empty)
    }

    private fun finishRefreshLayout() {
        detailRefreshLayout.finishRefresh()
    }

    private fun showSnackbar(resId: Int) {
        Snackbar.make(detailRefreshLayout, getString(resId), Snackbar.LENGTH_SHORT)
                .apply {view.layoutParams = (view.layoutParams as ViewGroup.MarginLayoutParams).apply {setMargins(leftMargin, topMargin, rightMargin, bottomAppBar.height + fab.height)}}
                .show()
    }


    override fun onItemClick(view: View, position: Int): Boolean {
        startReader(position)
        return false
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
        manga.last_update = chapters.maxBy { it.chapter.date_updated }?.chapter?.date_updated ?: 0L
        setMangaLastUpdated(manga)
        finishRefreshLayout()
        hideProgressBar()
        if(page == 1) {
            adapter.clear()
            resetProgressItem()
        }
        adapter.onLoadMoreComplete(chapters)
    }

    private fun resetProgressItem() {
        progressItem = ProgressItem()
        adapter?.endlessTargetCount = 0
        adapter?.setEndlessScrollListener(this, progressItem!!)
    }

    fun onAddPageError(throwable: Throwable) {
        finishRefreshLayout()
        chaptersProgressBar.gone()
        adapter?.onLoadMoreComplete(null)
        adapter?.endlessTargetCount = 1
        if(throwable is NoMoreResultException) {
            progressItem
            showSnackbar(R.string.snackbar_result_empty)
        } else {
            chaptersRecyclerView.gone()
            emptyChapters.visible()
            showSnackbar(R.string.snackbar_result_empty)
        }
    }

    override fun noMoreLoad(newItemsSize: Int) {

    }

    override fun onLoadMore(lastPosition: Int, currentPage: Int) {
        if (presenter.hasNextPage()) {
            presenter.requestNext()
        } else {
            adapter?.onLoadMoreComplete(null)
            adapter?.endlessTargetCount = 1
        }
    }

    fun setLastChanged(manga: Manga) {
        val adapter = adapter ?: return
        presenter.manga?.last_read_chapter_id = manga.last_read_chapter_id
        adapter.setLast(manga.last_read_chapter_id)
    }

    companion object {

        fun createIntent(context: Context, source: Long, cid: String): Intent {
            val intent = Intent(context, DetailActivity::class.java)
            intent.putExtra(Extras.EXTRA_SOURCE, source)
            intent.putExtra(Extras.EXTRA_CID, cid)
            return intent
        }
    }
}