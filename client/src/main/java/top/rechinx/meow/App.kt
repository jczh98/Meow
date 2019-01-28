package top.rechinx.meow

import android.app.Application
import com.jaredrummler.cyanea.Cyanea
import dagger.android.AndroidInjector
import dagger.android.DaggerApplication
import timber.log.Timber
import top.rechinx.meow.di.*

class App : DaggerApplication() {

    override fun onCreate() {
        super.onCreate()

        INSTANCE = this

        // For timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Configure for cyanea
        Cyanea.init(this, resources)
    }

    private val androidInjector: AndroidInjector<out DaggerApplication> by lazy {
        DaggerAppComponent.builder()
                .application(this)
                .build()
    }

    override fun applicationInjector(): AndroidInjector<out DaggerApplication>
        = androidInjector

    fun appComponent() = androidInjector as DaggerAppComponent

    companion object {
        @Volatile
        private var INSTANCE: App? = null

        fun get() = INSTANCE!!
    }
}