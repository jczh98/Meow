package top.rechinx.meow

import android.app.Application
import com.facebook.stetho.Stetho
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.utils.Log
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import top.rechinx.meow.di.AppComponent

class App: Application() {

    private var basePath: String? = null

    override fun onCreate() {
        super.onCreate()
        instance = this
        Stetho.initializeWithDefaults(this)

        // For timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }
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


    companion object {

        lateinit var instance: App

    }
}