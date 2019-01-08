package top.rechinx.meow.di

import org.koin.dsl.module.module
import top.rechinx.meow.core.source.SourceManager

val appModule = module(createOnStart = true) {
    single { SourceManager() }
}