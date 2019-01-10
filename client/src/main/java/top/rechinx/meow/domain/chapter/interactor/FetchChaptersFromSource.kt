package top.rechinx.meow.domain.chapter.interactor

import io.reactivex.Flowable
import io.reactivex.Single
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.data.manga.mapper.convertToInfo
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga

class FetchChaptersFromSource(
        private val getOrAddChapter: GetOrAddChapter
) {

    fun interact(source: Source, manga: Manga, page: Int): Single<PagedList<Chapter>> {
        return Single.defer {
            val mangaInfo = manga.convertToInfo()

            val sourceChapterList = source.fetchChapterList(mangaInfo, page)

            Flowable.fromIterable(sourceChapterList.list)
                    .concatMapSingle { getOrAddChapter.interact(it, manga.id) }
                    .toList()
                    .map { PagedList(it, sourceChapterList.hasNextPage) }
        }
    }
}