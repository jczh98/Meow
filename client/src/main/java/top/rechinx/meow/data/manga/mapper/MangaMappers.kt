package top.rechinx.meow.data.manga.mapper

import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.data.manga.model.MangaEntity
import top.rechinx.meow.domain.manga.model.Manga

/**
 * Convert [MangaInfo] to [MangaEntity] by adding [sourceId]
 */
internal fun MangaInfo.convertToEntity(sourceId: Long): MangaEntity {
    return MangaEntity(
            id = 0,
            source = sourceId,
            key = key,
            title = title,
            artist = artist,
            author = author,
            description = description,
            genres = genres,
            status = status,
            cover = cover
    )
}

internal fun MangaEntity.convertToManga(): Manga {
    return Manga(
            id = id,
            source = source,
            key = key,
            title = title,
            artist = artist,
            author = author,
            description = description,
            genres = genres,
            status = status,
            cover = cover,
            favorite = favorite,
            last_chapter_id = last_chapter_id,
            lastUpdate = lastUpdate,
            viewer = viewer,
            last_chapter_key = last_chapter_key,
            viewed = viewed,
            downloaded = downloaded
    )
}

internal fun Manga.convertToEntity(): MangaEntity {
    return MangaEntity(
            id = id,
            source = source,
            key = key,
            title = title,
            artist = artist,
            author = author,
            description = description,
            genres = genres,
            status = status,
            cover = cover,
            favorite = favorite,
            last_chapter_id = last_chapter_id,
            lastUpdate = lastUpdate,
            viewer = viewer,
            last_chapter_key = last_chapter_key,
            viewed = viewed,
            downloaded = downloaded
    )
}

internal fun Manga.convertToInfo(): MangaInfo {
    return MangaInfo(
            key = key,
            title = title,
            artist = artist,
            author = author,
            description = description,
            genres = genres,
            status = status,
            cover = cover
    )
}
