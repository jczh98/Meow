package top.rechinx.meow.data.chapter.mapper

import top.rechinx.meow.core.source.model.ChapterInfo
import top.rechinx.meow.data.chapter.model.ChapterEntity
import top.rechinx.meow.domain.chapter.model.Chapter

internal fun ChapterEntity.convertToChapter(): Chapter {
    return Chapter(
            id,
            mangaId,
            key,
            name,
            progress,
            date_upload,
            number,
            download,
            complete
    )
}

internal fun ChapterInfo.convertToEntity(mangaId: Long): ChapterEntity {
    return ChapterEntity(
            0,
            mangaId,
            key,
            name,
            0,
            dateUpload
    )
}

internal fun Chapter.convertToChapterInfo(): ChapterInfo {
    return ChapterInfo(
            key,
            name,
            0
    )
}