package top.rechinx.meow.data.chapter

import io.reactivex.Maybe
import io.reactivex.Single
import top.rechinx.meow.core.source.model.ChapterInfo
import top.rechinx.meow.data.chapter.dao.ChapterDao
import top.rechinx.meow.data.chapter.mapper.convertToChapter
import top.rechinx.meow.data.chapter.mapper.convertToEntity
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.chapter.repository.ChapterRepository
import javax.inject.Inject

class ChapterRepositoryImpl @Inject constructor(
        private val chapterDao: ChapterDao
) : ChapterRepository {

    override fun getChapter(chapterKey: String, mangaId: Long): Maybe<Chapter> {
        return chapterDao.queryChapter(chapterKey, mangaId)
                .map { it.convertToChapter() }
    }

    override fun getChapters(mangaId: Long): Maybe<List<Chapter>> {
        return chapterDao.getChapters(mangaId)
                .map { list ->
                    list.map {
                        it.convertToChapter()
                    }
                }
    }

    override fun saveAndLoadChapter(chapterInfo: ChapterInfo, mangaId: Long): Single<Chapter> {
        val newChapter = chapterInfo.convertToEntity(mangaId)

        return Single.create {
            val insertedId = chapterDao.insertChapter(newChapter)
            val dbChapter = newChapter.copy(id = insertedId)
            it.onSuccess(dbChapter.convertToChapter())
        }
    }

}