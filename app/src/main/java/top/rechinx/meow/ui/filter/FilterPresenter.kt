package top.rechinx.meow.ui.filter

import android.os.Bundle
import androidx.paging.PagedList
import androidx.paging.RxPagedListBuilder
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.flexibleadapter.items.ISectionable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.ui.filter.items.HeaderItem
import top.rechinx.meow.ui.filter.items.SelectItem
import top.rechinx.meow.ui.filter.paging.FilterDataSourceFactory
import top.rechinx.rikka.mvp.BasePresenter

class FilterPresenter(sourceId: Long): BasePresenter<FilterActivity>(), KoinComponent {

    var sourceFilters = FilterList()
        set(value) {
            field = value
            filterItems = value.toItems()
        }

    var filterItems: List<IFlexible<*>> = emptyList()

    var appliedFilters = FilterList()

    var query = ""
        private set

    val sourceManager by inject<SourceManager>()

    val source = sourceManager.get(sourceId) as Source

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)
        sourceFilters = source.getFilterList()
        restartPaging()
    }

    fun restartPaging(query: String = this.query, filters: FilterList = this.appliedFilters) {
        this.appliedFilters = filters
        this.query = query
        val sourceFractory = FilterDataSourceFactory(source, query, filters)
        val config = PagedList.Config.Builder()
                .setPageSize(100)
                .setEnablePlaceholders(false)
                .build()
        RxPagedListBuilder(sourceFractory, config)
                .setFetchScheduler(Schedulers.io())
                .buildObservable()
                .cache()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeReplay({ view, item ->
                    view.onMangaLoaded(item)
                }, FilterActivity::onMangaLoadError)
    }

    fun syncSourceFilters(sourceId: Long) {

        sourceFilters = source.getFilterList()
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

    private fun FilterList.clone(): FilterList {
        return FilterList(mapNotNull {
            when (it) {
                is Filter.Header -> Filter.Header(it.name)
                is Filter.Select<*> -> Filter.Select(it.name, it.values, it.state)
            }
        })
    }
}