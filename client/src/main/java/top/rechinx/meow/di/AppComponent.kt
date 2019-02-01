package top.rechinx.meow.di

import android.app.Application
import dagger.BindsInstance
import dagger.Component
import dagger.android.AndroidInjector
import dagger.android.support.AndroidSupportInjectionModule
import top.rechinx.meow.App
import top.rechinx.meow.di.modules.*
import javax.inject.Singleton

@Singleton
@Component(
        modules = [
            AppModule::class,
            AndroidSupportInjectionModule::class,
            RepositoryModule::class,
            DatabaseModule::class,
            MainActivityModule.MainActivityBuilder::class,
            MangaInfoActivityModule.MangaInfoActivityBuilder::class,
            ReaderActivityModule.ReaderActivityBuilder::class
        ]
)
interface AppComponent : AndroidInjector<App> {
    @Component.Builder
    interface Builder {
        @BindsInstance fun application(application: Application): Builder

        fun build(): AppComponent
    }

    override fun inject(app: App)

    fun glideComponentBuilder(): GlideComponent.Builder
}
