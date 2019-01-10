package top.rechinx.meow.domain.manga.repository

import io.reactivex.Maybe
import io.reactivex.Single
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.data.manga.model.MangaEntity
import top.rechinx.meow.domain.manga.model.Manga

interface MangaRepository {

    /**
     * Save manga from [mangaInfo] and return the manga which comes from database
     */
    fun saveAndReturnManga(mangaInfo: MangaInfo, sourceId: Long) : Single<Manga>

    /**
     * Get manga by [key] and [sourceId]
     */
    fun getManga(key: String, sourceId: Long): Maybe<Manga>
}