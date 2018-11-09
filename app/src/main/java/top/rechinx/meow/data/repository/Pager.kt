package top.rechinx.meow.data.repository

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import top.rechinx.meow.core.source.model.PagedList

abstract class Pager<T>(var currentPage: Int = 1) {

    var hasNextPage = true
        private set

    val results = PublishRelay.create<Pair<Int, List<T>>>()

    abstract fun requestNext(): Observable<PagedList<T>>

    fun onPageReceived(pages: PagedList<T>) {
        val page = currentPage
        currentPage++
        hasNextPage = pages.hasNextPage && pages.list.isNotEmpty()
        results.accept(Pair(page, pages.list))
    }

}