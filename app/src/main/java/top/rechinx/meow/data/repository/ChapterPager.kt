package top.rechinx.meow.data.repository

import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.AbsChapter
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.exception.NoMoreResultException

class ChapterPager(val source: Source, val cid: String): Pager<AbsChapter>() {

    override fun requestNext(): Observable<PagedList<AbsChapter>> {
        val page = currentPage

        return source.fetchChapters(page, cid)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if(it.list.isNotEmpty()) {
                        onPageReceived(it)
                    } else {
                        throw NoMoreResultException()
                    }
                }
    }

}