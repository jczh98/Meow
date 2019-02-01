package top.rechinx.meow.domain.page.loader

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.processors.PublishProcessor
import io.reactivex.rxkotlin.plusAssign
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.PageInfo
import top.rechinx.meow.data.cache.ChapterCache
import top.rechinx.meow.data.chapter.mapper.convertToChapterInfo
import top.rechinx.meow.domain.page.model.ReaderChapter
import top.rechinx.meow.domain.page.model.ReaderPage
import java.util.concurrent.PriorityBlockingQueue
import java.util.concurrent.atomic.AtomicInteger

class HttpPageLoader(private val chapter: ReaderChapter,
                     private val source: HttpSource,
                     private val chapterCache: ChapterCache): PageLoader() {

    private val queue = PriorityBlockingQueue<PriorityPage>()

    private val disposable = CompositeDisposable()

    init {
        disposable += Observable.defer { Observable.just(queue.take().page) }
                .filter { it.status == PageInfo.QUEUE }
                .concatMap {
                    source.fetchImageFromCacheThenNet(it)
                }
                .repeat()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                }, {
                    if(it !is InterruptedException) {
                        Timber.e(it.message)
                    }
                })
    }

    override fun recycle() {
        super.recycle()
        disposable.clear()
        queue.clear()
    }

    override fun getPages(): Observable<List<ReaderPage>> {
        return Observable.just(source.fetchPageList(chapter.chapter.convertToChapterInfo())
                .mapIndexed { index, page ->
                    ReaderPage(index, page.url, page.imageUrl)
                })
    }

    override fun getPage(page: ReaderPage): Observable<Int> {
        return Observable.defer {
            val imageUrl = page.imageUrl

            // Check if the image has been deleted
            if (page.status == PageInfo.READY && imageUrl != null && !chapterCache.isImageInCache(imageUrl)) {
                page.status = PageInfo.QUEUE
            }

            // Automatically retry failed pages when subscribed to this page
            if (page.status == PageInfo.ERROR) {
                page.status = PageInfo.QUEUE
            }

            val statusProcessor = PublishProcessor.create<Int>().toSerialized()
            page.setStatusProcessor(statusProcessor)

            if (page.status == PageInfo.QUEUE) {
                queue.offer(PriorityPage(page, 1))
            }

            preloadNextPages(page, 4)

            statusProcessor.startWith(page.status).toObservable()
        }
    }

    private fun preloadNextPages(currentPage: ReaderPage, amount: Int) {
        val pageIndex = currentPage.index
        val pages = currentPage.chapter.pages ?: return
        if (pageIndex == pages.lastIndex) return
        val nextPages = pages.subList(pageIndex + 1, Math.min(pageIndex + 1 + amount, pages.size))
        for (nextPage in nextPages) {
            if (nextPage.status == PageInfo.QUEUE) {
                queue.offer(PriorityPage(nextPage, 0))
            }
        }
    }

    override fun retryPage(page: ReaderPage) {
        if (page.status == PageInfo.ERROR) {
            page.status = PageInfo.QUEUE
        }
        queue.offer(PriorityPage(page, 2))
    }

    private fun HttpSource.fetchImageFromCacheThenNet(page: ReaderPage): Observable<ReaderPage> {
        return if(page.imageUrl.isNullOrEmpty())
            getImageUrl(page).flatMap { getCachedImage(it) }
        else
            getCachedImage(page)
    }

    private fun HttpSource.getImageUrl(page: ReaderPage): Observable<ReaderPage> {
        page.status = PageInfo.LOAD_PAGE
        return fetchImageUrl(page)
                .doOnError { page.status = PageInfo.ERROR }
                .onErrorReturn { null }
                .doOnNext { page.imageUrl = it }
                .map { page }
    }

    private fun HttpSource.getCachedImage(page: ReaderPage): Observable<ReaderPage> {
        val imageUrl = page.imageUrl ?: return Observable.just(page)

        return Observable.just(page)
                .flatMap {
                    if (!chapterCache.isImageInCache(imageUrl)) {
                        cacheImage(page)
                    } else {
                        Observable.just(page)
                    }
                }
                .doOnNext {
                    page.stream = { chapterCache.getImageFile(imageUrl).inputStream() }
                    page.status = PageInfo.READY
                }
                .doOnError { page.status = PageInfo.ERROR }
                .onErrorReturn { page }
    }

    private fun HttpSource.cacheImage(page: ReaderPage): Observable<ReaderPage> {
        page.status = PageInfo.DOWNLOAD_IMAGE
        return fetchImage(page)
                .doOnNext { chapterCache.putImageToCache(page.imageUrl!!, it) }
                .map { page }
    }

    private data class PriorityPage(
            val page: ReaderPage,
            val priority: Int
    ): Comparable<PriorityPage> {

        companion object {
            private val idGenerator = AtomicInteger()
        }

        private val identifier = idGenerator.incrementAndGet()

        override fun compareTo(other: PriorityPage): Int {
            val p = other.priority.compareTo(priority)
            return if (p != 0) p else identifier.compareTo(other.identifier)
        }

    }
}