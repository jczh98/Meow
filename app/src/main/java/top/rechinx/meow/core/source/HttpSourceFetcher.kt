package top.rechinx.meow.core.source

import io.reactivex.Observable
import top.rechinx.meow.core.source.model.MangaPage

fun HttpSource.getImageUrl(page: MangaPage): Observable<MangaPage> {
    page.status = MangaPage.LOAD_PAGE
    return fetchImageUrl(page)
        .doOnError { page.status = MangaPage.ERROR }
        .onErrorReturn { null }
        .doOnNext { page.imageUrl = it }
        .map { page }
}

fun HttpSource.fetchAllImageUrlsFromPageList(pages: List<MangaPage>): Observable<MangaPage> {
    return Observable.fromIterable(pages)
            .filter { !it.imageUrl.isNullOrEmpty() }
            .mergeWith(fetchRemainingImageUrlsFromPageList(pages))
}

fun HttpSource.fetchRemainingImageUrlsFromPageList(pages: List<MangaPage>): Observable<MangaPage> {
    return Observable.fromIterable(pages)
            .filter { it.imageUrl.isNullOrEmpty() }
            .concatMap { getImageUrl(it) }
}
