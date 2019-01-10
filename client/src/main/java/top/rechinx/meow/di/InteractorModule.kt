package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.domain.catalog.interactor.GetLocalCatalogs
import top.rechinx.meow.domain.chapter.interactor.FetchChaptersFromSource
import top.rechinx.meow.domain.chapter.interactor.GetOrAddChapter
import top.rechinx.meow.domain.manga.interactor.GetManga
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.domain.manga.interactor.GetOrAddManga
import top.rechinx.meow.domain.manga.interactor.UpdateManga

val interactorModule = module {
    single { GetLocalCatalogs(get()) }
    single { GetOrAddManga(get()) }
    single { GetMangasListFromSource(get()) }
    single { GetManga(get()) }
    single { UpdateManga(get(), get()) }
    single { GetOrAddChapter(get()) }
    single { FetchChaptersFromSource(get()) }
}