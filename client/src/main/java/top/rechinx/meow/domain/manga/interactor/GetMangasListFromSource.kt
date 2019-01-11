package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.functions.Function
import io.reactivex.rxkotlin.flatMapIterable
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.data.manga.mapper.convertToEntity
import top.rechinx.meow.data.manga.model.MangaEntity
import top.rechinx.meow.domain.manga.model.Manga
import javax.inject.Inject

class GetMangasListFromSource @Inject constructor(
        private val getOrAddManga: GetOrAddManga
) {

    fun interact(source: CatalogSource, page: Int): Single<PagedList<Manga>> {
        return Single.defer {
            val sourceMangaList = source.fetchPopularMangas(page)

            Flowable.fromIterable(sourceMangaList.list)
                    .concatMapSingle { getOrAddManga.interact(it, source.id) }
                    .toList()
                    .map { PagedList(it, sourceMangaList.hasNextPage) }
        }
    }

}