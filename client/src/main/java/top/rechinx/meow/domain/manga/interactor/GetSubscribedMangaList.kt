package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Flowable
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.repository.MangaRepository
import javax.inject.Inject

class GetSubscribedMangaList @Inject constructor(
        private val mangaRepository: MangaRepository
) {

    fun interact(): Flowable<List<Manga>> {
        return mangaRepository.getSubscribedMangaList()
    }
}