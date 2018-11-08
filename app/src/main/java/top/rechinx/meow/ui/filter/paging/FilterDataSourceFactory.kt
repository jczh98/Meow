package top.rechinx.meow.ui.filter.paging

import androidx.paging.DataSource
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.data.database.model.Manga

class FilterDataSourceFactory(private val source: Source,
                              private val query: String,
                              private val filterList: FilterList) : DataSource.Factory<Int, Manga>() {

    override fun create(): DataSource<Int, Manga> {
        return FilterDataSource(source, query, filterList)
    }

}