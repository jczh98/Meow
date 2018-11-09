package top.rechinx.meow.ui.filter

import android.os.Bundle
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.ISectionable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.repository.CataloguePager
import top.rechinx.meow.support.log.L
import top.rechinx.meow.ui.filter.items.CatalogueItem
import top.rechinx.meow.ui.filter.items.HeaderItem
import top.rechinx.meow.ui.filter.items.SelectItem
import top.rechinx.rikka.mvp.BasePresenter

class FilterPresenter(sourceId: Long): BasePresenter<FilterActivity>(), KoinComponent {

    val sourceManager by inject<SourceManager>()

    val source = sourceManager.get(sourceId) as Source

    private lateinit var pager: CataloguePager

    private var pagerDisposable: Disposable? = null

    var sourceFilters = FilterList()
        set(value) {
            field = value
            filterItems = value.toItems()
        }

    var filterItems: List<IFlexible<*>> = emptyList()

    var appliedFilters = FilterList()

    var query = ""
        private set

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        sourceFilters = source.getFilterList()
        restartPager()
    }

    fun restartPager(query: String = this.query, filters: FilterList = this.appliedFilters) {
        pager = CataloguePager(source, query, filters)
        pager.results()
                .observeOn(Schedulers.io())
                .map {
                    it.first to it.second.map {
                    val manga = Manga()
                    manga.copyFrom(it)
                    manga.sourceId = source.id
                    manga
                } }
                .map { it.first to it.second.map { CatalogueItem(it) } }
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeReplay({ view, (page, mangas) ->
                    view.onAddPage(page, mangas)
                }, { _, error ->
                    error.printStackTrace()
                    L.d(error.message)
                })

        // request first page
        requestNext()
    }

   fun requestNext() {
        if(!hasNextPage()) return
        Observable.defer { pager.requestNext() }
                .subscribeFirst({ _, _ -> }, FilterActivity::onAddPageError)
    }

    fun hasNextPage(): Boolean {
        return pager.hasNextPage
    }


    private fun FilterList.toItems(): List<IFlexible<*>> {
        return mapNotNull {
            when (it) {
                is Filter.Header -> HeaderItem(it)
                is Filter.Select<*> -> SelectItem(it)
//                is Filter.Group<*> -> {
//                    val group = GroupItem(it)
//                    val subItems = it.state.mapNotNull {
//                        when (it) {
//                            is Filter.Select<*> -> SelectSectionItem(it)
//                            else -> null
//                        } as? ISectionable<*, *>
//                    }
//                    subItems.forEach { it.header = group }
//                    group.subItems = subItems
//                    group
//                }
            }
        }
    }
}