package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Flowable
import io.reactivex.Single
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.domain.manga.model.Manga
import javax.inject.Inject

class SearchMangasListFromSource @Inject constructor(
        private val getOrAddManga: GetOrAddManga
) {

    fun interact(source: CatalogSource,
                 query: String,
                 filters: FilterList,
                 page: Int) : Single<PagedList<Manga>> = Single.defer {
        val sourcePage = source.fetchSearchMangas(query, page, filters)

        Flowable.fromIterable(sourcePage.list)
                .concatMapSingle { getOrAddManga.interact(it, source.id) }
                .toList()
                .map { PagedList(it, sourcePage.hasNextPage) }
    }
}