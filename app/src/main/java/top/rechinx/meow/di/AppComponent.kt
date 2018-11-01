package top.rechinx.meow.di

object AppComponent {

    fun modules() = listOf(AppModule.appModule,
            ViewModelModule.viewModelModule,
            DatabaseModule.databseModule,
            PresenterModule.presenterModule)
}