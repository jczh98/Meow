package top.rechinx.meow.ui.reader.viewer.pager

import androidx.viewpager.widget.ViewPager
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.domain.page.model.ChapterTransition
import top.rechinx.meow.domain.page.model.ReaderPage
import top.rechinx.meow.domain.page.model.ViewerChapters
import top.rechinx.meow.rikka.ext.gone
import top.rechinx.meow.rikka.ext.visible
import top.rechinx.meow.ui.reader.ReaderActivity
import top.rechinx.meow.ui.reader.viewer.BaseViewer

abstract class PagerViewer(val activity: ReaderActivity,
                           val preferences: PreferenceHelper) : BaseViewer {

    val pager = createPager()

    val config = PagerConfig(this, preferences)

    private val adapter = PagerViewerAdapter(this)

    private var currentPage: Any? = null

    private var awaitingIdleViewerChapters: ViewerChapters? = null

    private var isIdle = true
        set(value) {
            field = value
            if (value) {
                awaitingIdleViewerChapters?.let {
                    setChaptersInternal(it)
                    awaitingIdleViewerChapters = null
                }
            }
        }

    abstract fun createPager(): Pager

    override fun getView(): View = pager

    init {
        pager.gone()
        pager.layoutParams = ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT)
        pager.offscreenPageLimit = 1
        pager.adapter = adapter
        pager.addOnPageChangeListener(object : ViewPager.SimpleOnPageChangeListener() {
            override fun onPageSelected(position: Int) {
                val page = adapter.items.getOrNull(position)
                if (page != null && currentPage != page) {
                    currentPage = page
                    when (page) {
                        is ReaderPage -> onPageSelected(page, position)
                        is ChapterTransition -> onTransitionSelected(page)
                    }
                }
            }

            override fun onPageScrollStateChanged(state: Int) {
                isIdle = state == ViewPager.SCROLL_STATE_IDLE
            }
        })
        pager.tapListener = { event ->
            val positionX = event.x
            when {
                positionX < pager.width * 0.33f -> moveLeft()
                positionX > pager.width * 0.66f -> moveRight()
                else -> activity.toggleMenu()
            }
        }
        pager.longTapListener = {
            val item = adapter.items.getOrNull(pager.currentItem)
            if (item is ReaderPage) {
                activity.onPageLongTap(item)
            }
        }
    }

    override fun destroy() {
        super.destroy()
        config.clear()
    }

    /**
     * Moves to the page at the right.
     */
    protected open fun moveRight() {
        if (pager.currentItem != adapter.count - 1) {
            pager.setCurrentItem(pager.currentItem + 1, config.usePageTransitions)
        }
    }

    /**
     * Moves to the page at the left.
     */
    protected open fun moveLeft() {
        if (pager.currentItem != 0) {
            pager.setCurrentItem(pager.currentItem - 1, config.usePageTransitions)
        }
    }

    /**
     * Moves to the page at the top (or previous).
     */
    protected open fun moveUp() {
        moveToPrevious()
    }

    /**
     * Moves to the page at the bottom (or next).
     */
    protected open fun moveDown() {
        moveToNext()
    }

    /**
     * Moves to the next page.
     */
    open fun moveToNext() {
        moveRight()
    }

    /**
     * Moves to the previous page.
     */
    open fun moveToPrevious() {
        moveLeft()
    }

    private fun onPageSelected(page: ReaderPage, position: Int) {
        val pages = page.chapter.pages!!
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
        if(isIdle) {
            setChaptersInternal(chapters)
        } else {
            awaitingIdleViewerChapters = chapters
        }
    }

    private fun setChaptersInternal(chapters: ViewerChapters) {
        adapter.setChapters(chapters)
        if(pager.visibility == View.GONE) {
            val pages = chapters.currChapter.pages ?: return
            moveToPage(pages[chapters.currChapter.requestedPage])
            pager.visible()
        }
    }

    override fun moveToPage(page: ReaderPage) {
        val position = adapter.items.indexOf(page)
        if (position != -1) {
            pager.setCurrentItem(position, true)
        }
    }

    override fun handleKeyEvent(event: KeyEvent): Boolean {
        val isUp = event.action == KeyEvent.ACTION_UP

        when (event.keyCode) {
            KeyEvent.KEYCODE_VOLUME_DOWN -> {
                if (!config.volumeKeysEnabled || activity.menuVisible) {
                    return false
                } else if (isUp) {
                    if (!config.volumeKeysInverted) moveDown() else moveUp()
                }
            }
            KeyEvent.KEYCODE_VOLUME_UP -> {
                if (!config.volumeKeysEnabled || activity.menuVisible) {
                    return false
                } else if (isUp) {
                    if (!config.volumeKeysInverted) moveUp() else moveDown()
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