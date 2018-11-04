package top.rechinx.meow.ui.details

import android.content.Context
import android.content.Intent
import android.view.MenuItem
import com.google.android.material.snackbar.Snackbar
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import com.bumptech.glide.Glide
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.scwang.smartrefresh.header.MaterialHeader
import kotlinx.android.synthetic.main.activity_detail.*
import org.koin.android.ext.android.inject
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.global.Extras
import top.rechinx.meow.support.ext.gone
import top.rechinx.meow.support.ext.visible
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseActivity
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.ui.reader.ReaderActivity
import java.text.DateFormat
import java.util.*

class DetailActivity: BaseActivity(), DetailContract.View, BaseAdapter.OnItemClickListener {

    override val presenter: DetailContract.Presenter by inject()

    val sourceId: Long by lazy { intent.getLongExtra(Extras.EXTRA_SOURCE, 0) }
    val cid: String by lazy { intent.getStringExtra(Extras.EXTRA_CID) }

    private lateinit var adapter: DetailAdapter

    private var manga: Manga? = null
    private var needsChaptersRefresh: Boolean = true

    override fun onStart() {
        super.onStart()
        presenter.subscribe(this)
    }

    override fun onDestroy() {
        presenter.unsubscribe()
        super.onDestroy()
    }

    override fun initViews() {
        if(Toolbar != null) {
            setSupportActionBar(Toolbar)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
        }
        Toolbar?.setNavigationOnClickListener { finish() }
        chaptersRecyclerView.layoutManager = GridLayoutManager(this, 4)
        chaptersRecyclerView.setHasFixedSize(false)
        chaptersRecyclerView.itemAnimator = null
        adapter = DetailAdapter(this, ArrayList())
        adapter.setOnItemClickListener(this)
        chaptersRecyclerView.adapter = adapter
        // Refresh layout setup
        detailRefreshLayout.setRefreshHeader(MaterialHeader(this))
        detailRefreshLayout.setOnRefreshListener {
            chaptersProgressBar.visible()
            chaptersRecyclerView.gone()
            needsChaptersRefresh = true
            adapter.clear()
            presenter.fetchMangaInfo(sourceId, cid)
        }
        fab.setOnClickListener {
            if(manga?.last_read_chapter_id != -1L) {
                val chapter = adapter.datas.first { manga?.last_read_chapter_id == it.id }
                startReader(chapter, true)
            } else {
                startReader(-1, false)
                (it as FloatingActionButton).setImageResource(R.drawable.ic_continue_read_white_24dp)
            }
        }
        bottomAppBar.setOnMenuItemClickListener {
            when(it.itemId) {
                R.id.action_favorite -> {
                    if(manga != null) {
                        presenter.favoriteOrNot(manga!!)
                        if (manga!!.favorite)  {
                            bottomAppBar.replaceMenu(R.menu.bottombar_menu_with_unfavorite)
                        } else {
                            bottomAppBar.replaceMenu(R.menu.bottombar_menu_with_favorite)
                        }
                        manga!!.favorite = !manga!!.favorite
                    }
                }
            }
            return@setOnMenuItemClickListener true
        }
    }

    override fun onResume() {
        super.onResume()
        presenter.fetchMangaInfo(sourceId, cid, needsChaptersRefresh)
        if(needsChaptersRefresh) {
            needsChaptersRefresh = false
        }
    }

    private fun hideProgressBar() {
        chaptersProgressBar.gone()
        chaptersRecyclerView.visible()
    }

    override fun onMangaLoadCompleted(manga: Manga, needsChaptersRefresh: Boolean) {
        finishRefreshLayout()
        setManga(manga)
        adapter.manga = manga
        adapter.notifyDataSetChanged()
        if(needsChaptersRefresh) presenter.fetchMangaChapters(sourceId, cid)
    }

    private fun setManga(manga: Manga) {
        this.manga = manga
        mangaInfoTitle.text = manga.title
        Glide.with(this).load(manga).into(mangaInfoCover)
        Glide.with(this).load(manga).into(mangaBackCover)
        mangaInfoAuthor.text = manga.author
        var statusString = ""
        when(manga?.status) {
            AbsManga.ONGOING -> statusString += getString(R.string.string_manga_statu_ongoing)
            AbsManga.COMPLETED -> statusString += getString(R.string.string_manga_statu_completed)
            AbsManga.UNKNOWN -> statusString += getString(R.string.string_manga_statu_unknown)
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
        updateLabel += if(manga?.last_update != 0L) {
            DateFormat.getDateInstance(DateFormat.SHORT).format(Date(manga?.last_update!!))
        } else {
            getString(R.string.unknown)
        }
        mangaInfoUpdated.text = updateLabel
    }

    private fun setFAB(manga: Manga) {
        fab.setImageResource(if(manga.last_read_chapter_id == -1L) R.drawable.ic_start_white_24dp else R.drawable.ic_continue_read_white_24dp)
    }

    override fun onMangaFetchError() {
        finishRefreshLayout()
        hideProgressBar()
        showSnackbar(R.string.snackbar_result_empty)
    }

    override fun onChaptersInit(chapters: List<Chapter>) {
        // calculate manga update time
        this.manga?.last_update = chapters.maxBy { it.date_updated }?.date_updated ?: 0L
        adapter.manga = manga
        setMangaLastUpdated(manga!!)
        finishRefreshLayout()
        hideProgressBar()
        adapter.addAll(chapters)
    }

    override fun onChaptersFetchError() {
        finishRefreshLayout()
        chaptersProgressBar.gone()
        chaptersRecyclerView.gone()
        emptyChapters.visible()
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