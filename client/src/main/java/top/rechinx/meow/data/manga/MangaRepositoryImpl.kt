package top.rechinx.meow.data.manga

import io.reactivex.Completable
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
import javax.inject.Inject

class MangaRepositoryImpl @Inject constructor(
        private val mangaDao: MangaDao
) : MangaRepository {

    override fun saveAndReturnManga(mangaInfo: MangaInfo, sourceId: Long): Single<Manga> {
        val newManga = mangaInfo.convertToEntity(sourceId)
        return Single.create {
            val insertedId = mangaDao.insertManga(newManga)
            val dbManga = newManga.copy(id = insertedId)
            it.onSuccess(dbManga.convertToManga())
        }
    }

    override fun getManga(key: String, sourceId: Long): Maybe<Manga> {
        return mangaDao.queryManga(key, sourceId)
                .map {
                    it.convertToManga()
                }
    }

    override fun getManga(id: Long): Maybe<Manga> {
        return mangaDao.queryManga(id)
                .map { it.convertToManga() }
    }

    override fun updateManga(manga: Manga): Completable {
        return Completable.fromCallable {
            mangaDao.updateManga(manga.convertToEntity())
        }
    }
}