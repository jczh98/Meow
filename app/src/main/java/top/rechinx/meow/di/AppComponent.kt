package top.rechinx.meow.di

object AppComponent {

    fun modules() = listOf(AppModule.appModule,
            DatabaseModule.databseModule,
            PresenterModule.presenterModule)
}