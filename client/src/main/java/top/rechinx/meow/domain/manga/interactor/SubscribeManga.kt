package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Maybe
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.repository.MangaRepository
import top.rechinx.meow.rikka.rx.RxSchedulers
import javax.inject.Inject

class SubscribeManga @Inject constructor(
        private val mangaRepository: MangaRepository
) {

    fun interact(manga: Manga, flag: Boolean = true): Maybe<Manga> {
        return mangaRepository.subscribeManga(manga, flag)
    }
}