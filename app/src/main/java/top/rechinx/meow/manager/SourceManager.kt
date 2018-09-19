package top.rechinx.meow.manager

import android.content.Context
import android.util.SparseArray
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.Single
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.App
import top.rechinx.meow.core.Parser
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.model.Source
import top.rechinx.meow.source.Dmzj
import top.rechinx.meow.source.Kuaikan
import top.rechinx.meow.source.Shuhui
import top.rechinx.meow.support.relog.ReLog
import top.rechinx.meow.utils.FileUtils
import java.io.File
import java.util.*

class SourceManager {

    private var mDatabaseHelper: AppDatabase = AppDatabase.getInstance()
    private var mSources: SparseArray<SaSource> = SparseArray()

    fun initSource(): Completable {
        return Completable.fromCallable {
            mDatabaseHelper.sourceDao().insert(Dmzj.getDefaultSource(),
                    Shuhui.getDefaultSource(),
                    Kuaikan.getDefaultSource())
        }
    }

    fun list(): Single<List<Source>> = mDatabaseHelper.sourceDao().list()

    fun listEnable(): Single<List<Source>> = mDatabaseHelper.sourceDao().listEnable()


    fun update(source: Source): Completable {
        return Completable.fromCallable {
            mDatabaseHelper.sourceDao().update(source)
        }
    }

    fun load(type: Int) = mDatabaseHelper.sourceDao().load(type)

    fun identify(type: Int, title: String): Source {
        var source = mDatabaseHelper.sourceDao().identify(type, title)
        if(source == null) {
            source = Source(0, type, title, true)
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
            Kuaikan.TYPE -> return Kuaikan(source)
            else -> return null
        }
    }

    // for new engine
    fun rxGetSource(name: String): Observable<SaSource> {
        return Observable.just(getSource(name)).subscribeOn(Schedulers.io())
    }

    fun getSourceNames(): List<String> {
        val files = FileUtils.loadFiles(App.instance.getBasePath())
        val list = LinkedList<String>()
        for(i in 0 until files?.size!!) {
            list.add(files[i].name.replace(".xml", ""))
        }
        return list
    }

    fun getSource(name: String): SaSource {
        var source = mSources[name.hashCode()]
        if(source == null) {
            val xml = FileUtils.readTextFromSDcard(App.instance.getBasePath() + "/" + name + ".xml")
            source = loadSource(xml!!)
        }
        return source
    }

    fun loadSource(xml: String): SaSource? {
        var source = parserSource(xml)
        if(source != null) {
            mSources.put(source.title.hashCode(),source)
        }
        return source
    }

    fun parserSource(xml: String): SaSource? {
        return try {
            SaSource(App.instance, xml)
        } catch (e: Exception) {
            e.printStackTrace()
            null
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