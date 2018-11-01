package top.rechinx.meow.data.repository

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga

class ChapterRepository(val sourceManager: SourceManager,
                        val chapterDao: ChapterDao) {

    fun getLocalChapters(manga: Manga) : List<Chapter> {
        return chapterDao.getChapters(manga.id).blockingGet()
    }

    fun updateChapter(chapter: Chapter) {
        Completable.fromAction {
            chapterDao.updateChapter(chapter)
        }.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

}