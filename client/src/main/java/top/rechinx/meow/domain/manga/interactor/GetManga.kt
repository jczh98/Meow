package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Maybe
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.repository.MangaRepository

class GetManga(
        private val mangaRepository: MangaRepository
) {

    fun interact(mangaId: Long): Maybe<Manga> {
        return mangaRepository.getManga(mangaId)
    }

    fun interact(mangaKey: String, sourceId: Long): Maybe<Manga> {
        return mangaRepository.getManga(mangaKey, sourceId)
    }
}