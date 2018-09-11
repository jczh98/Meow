package top.rechinx.meow.manager

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Single
import top.rechinx.meow.core.Parser
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.model.Source
import top.rechinx.meow.source.Dmzj
import top.rechinx.meow.source.Shuhui

class SourceManager {

    private var mDatabaseHelper: AppDatabase = AppDatabase.getInstance()

    fun initSource(): Completable {
        return Completable.fromCallable {
            mDatabaseHelper.sourceDao().insert(Dmzj.getDefaultSource(),
                    Shuhui.getDefaultSource())
        }
    }

    fun list(): Single<List<Source>> = mDatabaseHelper.sourceDao().list()

    fun load(type: Int) = mDatabaseHelper.sourceDao().load(type)

    fun identify(type: Int, title: String): Source {
        var source = mDatabaseHelper.sourceDao().identify(type, title)
        if(source == null) {
            source = Source(0, type, title)
        }
        return source
    }

    fun getTitle(type: Int): String {
        return getParser(type)?.getTitle()!!
    }

    fun getParser(type: Int): Parser? {
        val source = load(type)
        when(type) {
            Dmzj.TYPE -> return Dmzj(source)
            Shuhui.TYPE -> return Shuhui(source)
            else -> return null
        }
    }
    companion object {

        private var instance:SourceManager ?= null

        fun getInstance(): SourceManager {
            return instance ?: synchronized(this) {
                instance ?: SourceManager().also { instance = it }
            }
        }
    }
}