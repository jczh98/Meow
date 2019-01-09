package top.rechinx.meow.data.manga

import io.reactivex.Maybe
import io.reactivex.Single
import timber.log.Timber
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.data.manga.dao.MangaDao
import top.rechinx.meow.data.manga.mapper.convertToEntity
import top.rechinx.meow.data.manga.mapper.convertToManga
import top.rechinx.meow.data.manga.model.MangaEntity
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.manga.repository.MangaRepository

internal class MangaRepositoryImpl(
        private val mangaDao: MangaDao
) : MangaRepository {

    override fun saveAndReturnManga(mangaInfo: MangaInfo, sourceId: Long): Single<Manga> {
        val newManga = mangaInfo.convertToEntity(sourceId)
        return Single.create{
            val insertedId = mangaDao.insertManga(newManga)
            newManga.copy(id = insertedId)
            it.onSuccess(newManga.convertToManga())
        }
    }

    override fun getManga(key: String, sourceId: Long): Maybe<Manga> {
        return mangaDao.queryManga(key, sourceId)
                .map {
                    it.convertToManga()
                }
    }
}