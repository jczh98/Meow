package top.rechinx.meow.data.repository

import io.reactivex.Maybe
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.AbsChapter
import top.rechinx.meow.data.database.dao.ChapterDao
import top.rechinx.meow.data.database.dao.MangaDao
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import java.lang.Exception

class MangaRepository(private val sourceManager: SourceManager,
                      private val mangaDao: MangaDao,
                      private val chapterDao: ChapterDao) {

    fun getManga(mangaId: Long) : Observable<Manga> {
        return mangaDao.loadManga(mangaId).toObservable()
    }

    fun updateManga(manga: Manga) {
        Observable.just(mangaDao.updateManga(manga))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun fetchMangaInfo(sourceId: Long, cid: String): Observable<Manga> {
        val source = sourceManager.get(sourceId)
        val dbManga = mangaDao.loadManga(sourceId, cid)
        return source!!.fetchMangaInfo(cid)
                .subscribeOn(Schedulers.io())
                .doOnNext {
                    val manga = Manga()
                    manga.copyFrom(it)
                    manga.cid = cid
                    manga.sourceId = sourceId
                    dbManga?.id?.let { manga.id = it }
                    mangaDao.insertManga(manga)
                }
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    mangaDao.loadManga(sourceId, cid)!!
                }.onErrorReturn {
                    it.printStackTrace()
                    mangaDao.loadManga(sourceId, cid)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun fetchMangaChapters(sourceId: Long, cid: String): Observable<List<Chapter>> {
        val manga = mangaDao.loadManga(sourceId, cid)
        val source = sourceManager.get(sourceId)
        return source!!.fetchChapters(cid).map { syncChaptersWithSource(it, manga!!, source) }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .map {
                    chapterDao.getChapters(manga?.id!!).blockingGet()
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

    fun syncChaptersWithSource(rawSourceChapter: List<AbsChapter>, manga: Manga, source: Source) : List<Chapter> {
        if(rawSourceChapter.isEmpty()) {
            throw Exception("No chapters found")
        }

        val dbChapters = chapterDao.getChapters(manga.id).blockingGet()

        val sourceChapters = rawSourceChapter.mapIndexed { index, chapter ->
            Chapter.create().apply {
                copyFrom(chapter)
                manga_id = manga.id
            }
        }

        val toAdd = mutableListOf<Chapter>()
        val toChange = mutableListOf<Chapter>()

        for(sourceChapter in sourceChapters) {
            val dbChapter = dbChapters.find { it.url == sourceChapter.url }

            if(dbChapter == null) {
                toAdd.add(sourceChapter)
            } else {
                if(shouldUpdateDbChapter(dbChapter, sourceChapter)) {
                    dbChapter.name = sourceChapter.name
                    dbChapter.date_updated = sourceChapter.date_updated
                    dbChapter.chapter_number = sourceChapter.chapter_number
                    toChange.add(dbChapter)
                }
            }
        }

        val toDelete = dbChapters.filterNot { dbChapter ->
            sourceChapters.any { sourceChapter ->
                dbChapter.url == sourceChapter.url
            }
        }

        if(toAdd.isEmpty() && toDelete.isEmpty() && toChange.isEmpty()) {
            return emptyList()
        }

        Observable.fromCallable {
            if(!toDelete.isEmpty()) {
                chapterDao.deleteChapters(toDelete)
            }
            if(!toAdd.isEmpty()) {
                chapterDao.insertChapters(toAdd)
            }
            if(!toChange.isEmpty()) {
                chapterDao.insertChapters(toChange)
            }
        }.observeOn(Schedulers.io())
                .subscribeOn(AndroidSchedulers.mainThread())
                .subscribe()
        return toAdd.toList()
    }

    private fun shouldUpdateDbChapter(dbChapter: Chapter, sourceChapter: AbsChapter): Boolean {
        return dbChapter.name != sourceChapter.name ||
                dbChapter.date_updated != sourceChapter.date_updated ||
                dbChapter.chapter_number != sourceChapter.chapter_number
    }

}