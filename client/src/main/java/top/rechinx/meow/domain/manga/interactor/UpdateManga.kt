package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Maybe
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.repository.MangaRepository
import javax.inject.Inject

class UpdateManga @Inject constructor(
        private val mangaRepository: MangaRepository,
        private val sourceManager: SourceManager
) {

    fun interact(source: Source, manga: Manga): Maybe<Manga> {
        val stubManga = MangaInfo(
                key = manga.key,
                title = manga.title,
                artist = manga.artist,
                author = manga.author,
                description = manga.description,
                genres = manga.genres,
                status = manga.status,
                cover = manga.cover
        )
        return Maybe.fromCallable { source.fetchMangaInfo(stubManga) }
                .flatMap { sourceManga ->
                    val updatedManga = manga.copy(
                            key = if (sourceManga.key.isEmpty()) manga.key else sourceManga.key,
                            title = if (sourceManga.title.isEmpty()) manga.title else sourceManga.title,
                            artist = sourceManga.artist,
                            author = sourceManga.author,
                            description = sourceManga.description,
                            genres = sourceManga.genres,
                            status = sourceManga.status,
                            cover = sourceManga.cover,
                            initialized = true
                    )
                    mangaRepository.updateManga(updatedManga)
                            .andThen(Maybe.just(updatedManga))
                }
    }

    fun interact(manga: Manga): Maybe<Manga> {
        val source = sourceManager.get(manga.source) ?: return Maybe.empty()
        return interact(source, manga)
    }

}