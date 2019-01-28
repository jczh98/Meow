package top.rechinx.meow.di.modules

import androidx.fragment.app.FragmentActivity
import dagger.Binds
import dagger.Module
import dagger.android.ContributesAndroidInjector
import top.rechinx.meow.ui.catalogbrowse.CatalogBrowseFragment
import top.rechinx.meow.ui.catalogs.CatalogsFragment
import top.rechinx.meow.ui.home.MainActivity
import top.rechinx.meow.ui.manga.MangaInfoActivity
import top.rechinx.meow.ui.manga.MangaInfoFragment

/**
 * For MainActivity with fragments
 */
@Module
abstract class MainActivityModule {

    @Binds
    abstract fun providesActivity(mainActivity: MainActivity): FragmentActivity

    @ContributesAndroidInjector(modules = [CatalogsFragmentModule::class])
    abstract fun contributeCatalogsFragment(): CatalogsFragment

    @ContributesAndroidInjector(modules = [CatalogBrowseFragmentModule::class, AssistedInjectModule::class])
    abstract fun contributeCatalogBrowseFragment(): CatalogBrowseFragment

    @Module
    abstract class MainActivityBuilder {
        @ContributesAndroidInjector(modules = [MainActivityModule::class])
        abstract fun contributeMainActivity(): MainActivity
    }
}

@Module
abstract class CatalogsFragmentModule

@Module
abstract class CatalogBrowseFragmentModule

/**
 * For MangaInfoActivity with fragments
 */
@Module
abstract class MangaInfoActivityModule {

    @Binds abstract fun providesActivity(mangaInfoActivity: MangaInfoActivity): FragmentActivity

    @ContributesAndroidInjector(modules = [MangaInfoFragmentModule::class, AssistedInjectModule::class])
    abstract fun contributeMangaInfoFragment(): MangaInfoFragment

    @Module
    abstract class MangaInfoActivityBuilder {
        @ContributesAndroidInjector(modules = [MangaInfoActivityModule::class, AssistedInjectModule::class])
        abstract fun contributeMangaInfoActivity(): MangaInfoActivity
    }
}

@Module
abstract class MangaInfoFragmentModule