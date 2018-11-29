package top.rechinx.meow.ui.reader.viewer.pager

import top.rechinx.meow.ui.reader.ReaderActivity

class L2RPagerViewer(activity: ReaderActivity) : PagerViewer(activity) {

    override fun createPager(): Pager = Pager(activity)

}

class R2LPagerViewer(activity: ReaderActivity): PagerViewer(activity) {

    override fun createPager(): Pager = Pager(activity)

    /**
     * Moves to the next page. On a R2L pager the next page is the one at the left.
     */
    override fun moveToNext() {
        moveLeft()
    }

    /**
     * Moves to the previous page. On a R2L pager the previous page is the one at the right.
     */
    override fun moveToPrevious() {
        moveRight()
    }
}