package top.rechinx.meow

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.os.Environment
import com.facebook.stetho.Stetho
import okhttp3.OkHttpClient
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.manager.SourceManager

class App: Application() {

    private var basePath: String? = null

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        instance = this
    }

    fun getBasePath(): String {
        if (basePath == null) {
            basePath = Environment.getExternalStorageDirectory().absolutePath + "/Meow/"
        }
        return basePath!!
    }

    companion object {

        lateinit var instance: App

        private var mHttpClient: OkHttpClient? = null

        fun getHttpClient(): OkHttpClient? {
            if(mHttpClient == null) {
                mHttpClient = OkHttpClient()
            }
            return mHttpClient
        }
    }
}