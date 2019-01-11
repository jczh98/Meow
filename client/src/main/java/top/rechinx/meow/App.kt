package top.rechinx.meow

import android.app.Application
import com.jaredrummler.cyanea.Cyanea
import timber.log.Timber
import toothpick.Toothpick
import toothpick.configuration.Configuration
import toothpick.registries.FactoryRegistryLocator
import toothpick.registries.MemberInjectorRegistryLocator
import toothpick.smoothie.module.SmoothieApplicationModule
import top.rechinx.meow.di.*

class App : Application() {

    override fun onCreate() {
        super.onCreate()

        // Dependency injection
        val config = if (BuildConfig.DEBUG) {
            Configuration.forDevelopment()
        } else {
            Configuration.forProduction()
        }
        Toothpick.setConfiguration(config.disableReflection())
        FactoryRegistryLocator.setRootRegistry(FactoryRegistry())
        MemberInjectorRegistryLocator.setRootRegistry(MemberInjectorRegistry())
        val scope = Toothpick.openScope(AppScope)
        scope.installModules(
                SmoothieApplicationModule(this),
                ApplicationModule,
                DataModule
        )

        // For timber
        if (BuildConfig.DEBUG) {
            Timber.plant(Timber.DebugTree())
        }

        // Configure for cyanea
        Cyanea.init(this, resources)
    }
}