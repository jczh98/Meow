package top.rechinx.meow

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import com.facebook.stetho.Stetho
import okhttp3.OkHttpClient
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.manager.SourceManager

class App: Application() {

    override fun onCreate() {
        super.onCreate()
        Stetho.initializeWithDefaults(this)
        instance = this
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