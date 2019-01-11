package top.rechinx.meow.domain.manga.interactor

import io.reactivex.Single
import timber.log.Timber
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.data.manga.model.MangaEntity
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.repository.MangaRepository
import javax.inject.Inject

class GetOrAddManga @Inject constructor(
        private val mangaRepository: MangaRepository
) {

    fun interact(mangaInfo: MangaInfo, sourceId: Long): Single<Manga> {
        return mangaRepository.getManga(mangaInfo.key, sourceId)
                .switchIfEmpty(Single.defer {
                    mangaRepository.saveAndReturnManga(mangaInfo, sourceId)
                })
    }
}