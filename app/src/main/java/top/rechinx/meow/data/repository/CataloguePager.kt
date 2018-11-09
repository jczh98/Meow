package top.rechinx.meow.data.repository

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.core.source.model.PagedManga
import top.rechinx.meow.exception.NoMoreResultException

class CataloguePager(val source: Source, val query: String, val filters: FilterList): Pager<AbsManga>() {

    override fun requestNext() : Observable<PagedList<AbsManga>> {
        val observable = if(query.isBlank() && filters.isEmpty()) {
            source.fetchPopularManga(currentPage)
        } else {
            source.fetchSearchManga(query, currentPage, filters)
        }

        return observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    if(it.list.isEmpty()) {
                        throw NoMoreResultException()
                    } else {
                        onPageReceived(it)
                    }
                }
    }
}