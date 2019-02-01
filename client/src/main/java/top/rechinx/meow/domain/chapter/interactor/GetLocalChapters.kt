package top.rechinx.meow.domain.chapter.interactor

import top.rechinx.meow.data.chapter.dao.ChapterDao
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.chapter.repository.ChapterRepository
import top.rechinx.meow.domain.manga.model.Manga
import javax.inject.Inject

class GetLocalChapters @Inject constructor(
        private val chapterRepository: ChapterRepository
) {
    fun interact(manga: Manga): List<Chapter> {
        return chapterRepository.getChapters(manga.id).blockingGet()
    }
}