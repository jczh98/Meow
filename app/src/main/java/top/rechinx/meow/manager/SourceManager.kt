package top.rechinx.meow.manager

import android.content.Context
import io.reactivex.Completable
import top.rechinx.meow.App
import top.rechinx.meow.core.Parser
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.dao.SourceDao
import top.rechinx.meow.model.Source
import top.rechinx.meow.source.Dmzj

class SourceManager {

    private var mDatabaseHelper: AppDatabase = AppDatabase.getInstance()

    fun initSource(): Completable {
        return Completable.fromCallable({
            var list = ArrayList<Source>()
            list.add(Dmzj.getDefaultSource())
            mDatabaseHelper.sourceDao().insert(list)
        })
    }

    fun load(type: Int) = mDatabaseHelper.sourceDao().load(type)

    fun getTitle(type: Int): String {
        return getParser(type)?.getTitle()!!
    }

    fun getParser(type: Int): Parser? {
        val source = load(type)
        when(type) {
            Dmzj.TYPE -> return Dmzj(source)
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