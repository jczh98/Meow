package top.rechinx.meow.ui.reader.viewer.webtoon

import androidx.recyclerview.widget.RecyclerView
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.WebtoonLayoutManager
import io.reactivex.disposables.CompositeDisposable
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.domain.page.model.ChapterTransition
import top.rechinx.meow.domain.page.model.ReaderPage
import top.rechinx.meow.domain.page.model.ViewerChapters
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.matchContent
import top.rechinx.meow.rikka.ext.visible
import top.rechinx.meow.ui.reader.ReaderActivity
import top.rechinx.meow.ui.reader.viewer.BaseViewer

class WebtoonViewer(val activity: ReaderActivity,
                    val preferences: PreferenceHelper): BaseViewer {

    val recyclerView = WebtoonRecyclerView(activity)

    val config = WebtoonConfig(preferences)

    val disposables = CompositeDisposable()

    private val frame = WebtoonFrame(activity)

    private val layoutManager = WebtoonLayoutManager(activity)

    private val adapter = WebtoonAdapter(this)

    private var scrollDistance = activity.resources.displayMetrics.heightPixels * 3 / 4

    private var currentPage: Any? = null

    init {
        recyclerView.gone()
        recyclerView.matchContent()
        recyclerView.itemAnimator = null
        recyclerView.layoutManager = layoutManager
        recyclerView.adapter = adapter
        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                val position = layoutManager.findLastEndVisibleItemPosition()
                val item = adapter.items.getOrNull(position)
                if (item != null && currentPage != item) {
                    currentPage = item
                    when (item) {
                        is ReaderPage -> onPageSelected(item, position)
                        is ChapterTransition -> onTransitionSelected(item)
                    }
                }

                if (dy < 0) {
                    val firstIndex = layoutManager.findFirstVisibleItemPosition()
                    val firstItem = adapter.items.getOrNull(firstIndex)
                    if (firstItem is ChapterTransition.Prev && firstItem.to != null) {
                        activity.requestPreloadChapter(firstItem.to)
                    }
                }
            }
        })
        recyclerView.tapListener = { event ->
            val positionX = event.rawX
            when {
                positionX < recyclerView.width * 0.33 -> scrollUp()
                positionX > recyclerView.width * 0.66 -> scrollDown()
                else -> activity.toggleMenu()
            }
        }
        recyclerView.longTapListener = { event ->
            val child = recyclerView.findChildViewUnder(event.x, event.y)
            val position = recyclerView.getChildAdapterPosition(child!!)
            val item = adapter.items.getOrNull(position)
            if (item is ReaderPage) {
                activity.onPageLongTap(item)
            }
        }
        frame.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        frame.addView(recyclerView)
    }

    override fun getView(): View = frame

    override fun destroy() {
        super.destroy()
        disposables.clear()
        config.unsubscribe()
    }

    private fun onPageSelected(page: ReaderPage, position: Int) {
        val pages = page.chapter.pages!! // Won't be null because it's the loaded chapter
        activity.onPageSelected(page)

        if (page === pages.last()) {
            val transition = adapter.items.getOrNull(position + 1) as? ChapterTransition.Next
            if (transition?.to != null) {
                activity.requestPreloadChapter(transition.to)
            }
        }
    }

    private fun onTransitionSelected(transition: ChapterTransition) {
        val toChapter = transition.to
        if (toChapter != null) {
            activity.requestPreloadChapter(toChapter)
        } else if (transition is ChapterTransition.Next) {
            // No more chapters, show menu because the user is probably going to close the reader
            activity.showMenu()
        }
    }

    override fun setChapters(chapters: ViewerChapters) {
        adapter.setChapters(chapters)
        if (recyclerView.visibility == View.GONE) {
            val pages = chapters.currChapter.pages ?: return
            moveToPage(pages[chapters.currChapter.requestedPage])
            recyclerView.visible()
        }
    }

    override fun moveToPage(page: ReaderPage) {
        val position = adapter.items.indexOf(page)
        if (position != -1) {
            recyclerView.scrollToPosition(position)
        }
    }

    private fun scrollUp() {
        recyclerView.smoothScrollBy(0, -scrollDistance)
    }

    private fun scrollDown() {
        recyclerView.smoothScrollBy(0, scrollDistance)
    }

    override fun handleKeyEvent(event: KeyEvent): Boolean {
        val isUp = event.action == KeyEvent.ACTION_UP

        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (!config.volumeKeysEnabled || activity.menuVisible) {
                    return false
                } else if (isUp) {
                    if (!config.volumeKeysInverted) scrollDown() else scrollUp()
                }
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (!config.volumeKeysEnabled || activity.menuVisible) {
                    return false
                } else if (isUp) {
                    if (!config.volumeKeysInverted) scrollUp() else scrollDown()
                }
            }
            else -> return false
        }
        return true
    }

    override fun handleGenericMotionEvent(event: MotionEvent): Boolean {
        return false
    }

}