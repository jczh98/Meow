package top.rechinx.meow.data.repository

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.SChapter
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.exception.NoMoreResultException

class ChapterPager(val source: Source, val cid: String): Pager<SChapter>() {

    override fun requestNext(): Observable<PagedList<SChapter>> {
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