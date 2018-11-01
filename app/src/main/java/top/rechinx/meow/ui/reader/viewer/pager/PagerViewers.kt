package top.rechinx.meow.ui.reader.viewer.pager

import top.rechinx.meow.ui.reader.ReaderActivity

class L2RPagerViewer(activity: ReaderActivity) : PagerViewer(activity) {

    override fun createPager(): Pager = Pager(activity)

}

class R2LPagerViewer(activity: ReaderActivity): PagerViewer(activity) {

    override fun createPager(): Pager = Pager(activity)

}