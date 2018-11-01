package top.rechinx.meow.ui.reader.loader

import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.log.L
import top.rechinx.meow.ui.reader.model.ReaderChapter
import java.lang.Exception

class ChapterLoader(private val manga: Manga,
                    private val source: Source) {

    fun loadChapter(chapter: ReaderChapter) : Completable {
        if(chapter.state is ReaderChapter.State.Loaded) {
            return Completable.complete()
        }
        return Completable.fromObservable(
            Observable.just(chapter)
                    .doOnNext {
                        chapter.state = ReaderChapter.State.Loading }
                    .observeOn(Schedulers.io())
                    .flatMap {
                        val loader = getPageLoader(it)
                        chapter.pageLoader = loader
                        loader.getPages().take(1)
                                .doOnNext { pages ->
                                    pages.forEach { it.chapter = chapter }
                                }
                    }
                    .observeOn(AndroidSchedulers.mainThread())
                    .doOnNext { pages ->
                        if (pages.isEmpty()) {
                            throw Exception("Page list is empty")
                        }
                        chapter.state = ReaderChapter.State.Loaded(pages)
                    }
        ).doOnError {
            chapter.state = ReaderChapter.State.Error(it)
        }
    }

    private fun getPageLoader(readerChapter: ReaderChapter): PageLoader {
        return HttpPageLoader(readerChapter, source as HttpSource)
    }
}