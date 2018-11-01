package top.rechinx.meow

import android.app.Application
import android.arch.persistence.room.Room
import android.content.Context
import android.os.Environment
import com.facebook.stetho.Stetho
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient
import org.koin.android.ext.android.startKoin
import top.rechinx.meow.dao.AppDatabase
import top.rechinx.meow.di.AppComponent
import top.rechinx.meow.manager.PreferenceManager
import top.rechinx.meow.manager.SourceManager

class App: Application() {

    private var basePath: String? = null
    private var mPreferenceManager: PreferenceManager? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)

        // for koin
        startKoin(this, AppComponent.modules())

    }

    fun getBasePath(): String {
        if (basePath == null) {
            basePath = getExternalFilesDir(null).absolutePath
            //basePath = Environment.getExternalStorageDirectory().absolutePath + "/Meow/"
        }
        return basePath!!
    }

    fun getPreferenceManager(): PreferenceManager {
        if(mPreferenceManager == null) {
            mPreferenceManager = PreferenceManager(applicationContext)
        }
        return mPreferenceManager!!
    }



    companion object {

        lateinit var instance: App

        private var mHttpClient: OkHttpClient? = null

        fun getHttpClientBuilder(): OkHttpClient.Builder {
            return OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor())
        }

        fun getHttpClient(): OkHttpClient? {
            if(mHttpClient == null) {
                mHttpClient = this.getHttpClientBuilder().build()
            }
            return mHttpClient
        }
    }
}