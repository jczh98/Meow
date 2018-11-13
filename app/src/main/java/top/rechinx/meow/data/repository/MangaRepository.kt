package top.rechinx.meow.data.repository

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.SChapter
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

    fun getRelayManga(sourceId: Long, url: String) : Flowable<Manga> {
        return mangaDao.relayManga(sourceId, url)
    }

    fun getManga(sourceId: Long, cid: String): Manga? {
        return mangaDao.loadManga(sourceId, cid)
    }

    fun updateManga(manga: Manga): Disposable {
        return Observable.just(mangaDao.updateManga(manga))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe()
    }

    fun listFavorite(): Flowable<List<Manga>> {
        return mangaDao.listFavorite()
    }

    fun listHistory(): Flowable<List<Manga>> {
        return mangaDao.listHistory()
    }

    fun fetchMangaInfo(sourceId: Long, url: String): Observable<Manga> {
        val source = sourceManager.getOrStub(sourceId)
        return source.fetchMangaInfo(url)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .doOnNext {
                    val manga = Manga()
                    val dbManga = mangaDao.loadManga(sourceId, url)
                    manga.copyFrom(it)
                    manga.url = url
                    manga.sourceId = sourceId
                    manga.sourceName = source.name
                    dbManga?.id?.let { manga.id = it }
                    dbManga?.favorite?.let { manga.favorite = it }
                    dbManga?.history?.let { manga.history = it }
                    dbManga?.last_read_chapter_id?.let { manga.last_read_chapter_id = it }
                    mangaDao.insertManga(manga)
                }
                .map {
                    return@map mangaDao.loadManga(sourceId, url)!!
                }.onErrorReturn {
                    it.printStackTrace()
                    mangaDao.loadManga(sourceId, url)
                }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
    }

//    fun fetchMangaChapters(sourceId: Long, url: String): Observable<List<Chapter>> {
//        val manga = mangaDao.loadManga(sourceId, url)
//        val source = sourceManager.getOrStub(sourceId)
//        return source.fetchChapters(url).map { syncChaptersWithSource(it, manga!!, source) }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//                .map {
//                    chapterDao.getChapters(manga?.id!!).blockingGet()
//                }
//                .subscribeOn(Schedulers.io())
//                .observeOn(AndroidSchedulers.mainThread())
//    }

    fun fetchLocalChapters(mangaId: Long) = chapterDao.getChapters(mangaId).blockingGet()

    fun  syncChaptersWithSource(rawSourceChapter: List<SChapter>, manga: Manga, source: Source) : List<Chapter> {
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

    private fun shouldUpdateDbChapter(dbChapter: Chapter, sourceChapter: SChapter): Boolean {
        return dbChapter.name != sourceChapter.name ||
                dbChapter.date_updated != sourceChapter.date_updated ||
                dbChapter.chapter_number != sourceChapter.chapter_number
    }

}