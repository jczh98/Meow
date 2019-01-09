package top.rechinx.meow

import android.app.Application
import com.jaredrummler.cyanea.Cyanea
import org.koin.android.ext.android.startKoin
import timber.log.Timber
import top.rechinx.meow.di.appModule
import top.rechinx.meow.di.interactorModule
import top.rechinx.meow.di.repositoryModule
import top.rechinx.meow.di.viewModelModule

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // For timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Configure for cyanea
        Cyanea.init(this, resources)

        // Configure for Koin
        startKoin(this, listOf(
                appModule,
                viewModelModule,
                repositoryModule,
                interactorModule))
    }
}