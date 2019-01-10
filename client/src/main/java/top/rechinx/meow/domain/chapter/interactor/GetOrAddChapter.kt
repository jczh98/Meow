package top.rechinx.meow.domain.chapter.interactor

import io.reactivex.Single
import top.rechinx.meow.core.source.model.ChapterInfo
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.chapter.repository.ChapterRepository

class GetOrAddChapter(
        private val chapterRepository: ChapterRepository
) {

    fun interact(chapterInfo: ChapterInfo, mangaId: Long): Single<Chapter> {
        return chapterRepository.getChapter(chapterInfo.key, mangaId)
                .switchIfEmpty(Single.defer {
                        chapterRepository.saveAndLoadChapter(chapterInfo, mangaId)
                    })
    }
}