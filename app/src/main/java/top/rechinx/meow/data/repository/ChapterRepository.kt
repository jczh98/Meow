package top.rechinx.meow.data.repository

import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga

class ChapterRepository(val sourceManager: SourceManager,
                        val chapterDao: ChapterDao) {

    fun getLocalChapters(manga: Manga) : List<Chapter> {
        return chapterDao.getChapters(manga.id).blockingGet()
    }

}