package top.rechinx.meow.domain.page.interact

import android.app.Application
import io.reactivex.Completable
import io.reactivex.Observable
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.data.cache.ChapterCache
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.domain.page.loader.HttpPageLoader
import top.rechinx.meow.domain.page.loader.PageLoader
import top.rechinx.meow.domain.page.model.ReaderChapter
import top.rechinx.meow.rikka.rx.RxSchedulers
import java.io.Reader
import java.lang.Exception
import javax.inject.Inject

class LoadChapter @Inject constructor(
        private val context: Application,
        private val chapterCache: ChapterCache,
        private val schedulers: RxSchedulers
) {

    fun interact(manga: Manga, source: Source, chapter: ReaderChapter, isContinue: Boolean): Completable {
        if(chapter.state is ReaderChapter.State.Loaded) {
            return Completable.complete()
        }

        return Completable.fromObservable(
                Observable.just(chapter)
                        .doOnNext{
                            chapter.state = ReaderChapter.State.Loading
                        }
                        .observeOn(schedulers.io)
                        .flatMap {
                            val loader = getPageLoader(it, source as HttpSource)
                            chapter.pageLoader = loader
                            loader.getPages().take(1)
                                    .doOnNext { pages ->
                                        pages.forEach { page ->
                                            page.chapter = chapter
                                        }
                                    }
                        }
                        .observeOn(schedulers.main)
                        .doOnNext { pages ->
                            if (pages.isEmpty()) {
                                throw Exception("Page list is empty")
                            }
                            chapter.state = ReaderChapter.State.Loaded(pages)
                            if (isContinue) {
                                chapter.requestedPage = chapter.chapter.progress
                            }
                        }
        )
                .doOnError {
                    chapter.state = ReaderChapter.State.Error(it)
                }
    }

    private fun getPageLoader(readerChapter: ReaderChapter, source: HttpSource): PageLoader {
        return HttpPageLoader(readerChapter, source, chapterCache)
    }
}