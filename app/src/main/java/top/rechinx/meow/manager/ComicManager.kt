package top.rechinx.meow.manager

import io.reactivex.Completable
import io.reactivex.Flowable
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.model.Comic
import top.rechinx.meow.support.relog.ReLog

class ComicManager {

    private var mDatabaseHelper: AppDatabase = AppDatabase.getInstance()

    fun loadOrCreate(source: String, cid: String): Comic {
        var comic = mDatabaseHelper.comicDao().identify(source, cid)
        return comic ?: Comic(source, cid)
    }

    fun identify(source: String, cid: String): Comic {
        var comic = mDatabaseHelper.comicDao().identify(source, cid)
        if(comic == null) {
            comic = Comic(source, cid)
        }
        return comic
    }

    fun listFavorite(): Flowable<List<Comic>> {
        return mDatabaseHelper.comicDao().listFavorite()
    }

    fun listHistory(): Flowable<List<Comic>> {
        return mDatabaseHelper.comicDao().listHistory()
    }

    fun insert(comic: Comic){
        mDatabaseHelper.comicDao().insert(comic)
    }

    fun update(comic: Comic) {
        mDatabaseHelper.comicDao().update(comic)
    }

    fun updateOrInsert(comic: Comic): Completable {
        return if(comic.id == 0.toLong()) {
            Completable.fromCallable {insert(comic)}
        } else {
            Completable.fromCallable {update(comic)}
        }
    }

    companion object {

        private var instance:ComicManager ?= null

        fun getInstance(): ComicManager {
            return instance ?: synchronized(this) {
                instance ?: ComicManager().also { instance = it }
            }
        }
    }
}