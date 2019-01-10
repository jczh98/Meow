package top.rechinx.meow.domain.chapter.repository

import io.reactivex.Maybe
import io.reactivex.Single
import top.rechinx.meow.core.source.model.ChapterInfo
import top.rechinx.meow.domain.chapter.model.Chapter

interface ChapterRepository {

    fun getChapter(chapterKey: String, mangaId: Long): Maybe<Chapter>

    fun saveAndLoadChapter(chapterInfo: ChapterInfo, mangaId: Long): Single<Chapter>
}