package top.rechinx.meow.ui.reader.loader

import android.app.Application
import io.reactivex.Observable
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.source.HttpSource
import top.rechinx.meow.core.source.model.MangaPage
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.download.DownloadProvider
import top.rechinx.meow.data.preference.PreferenceHelper
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.meow.ui.reader.model.ReaderChapter
import top.rechinx.meow.ui.reader.model.ReaderPage

class DownloadedPageLoader(private val chapter: ReaderChapter,
                           private val manga: Manga,
                           private val source: HttpSource) : PageLoader(), KoinComponent {

    val preferences by inject<PreferenceHelper>()
    val context by inject<Application> ()

    override fun getPages(): Observable<List<ReaderPage>> {
        return DownloadProvider.buildReaderPages(source, manga, chapter.chapter,
                preferences.downloadsDirectory().getOrDefault(),
                context.contentResolver)
    }

    override fun getPage(page: ReaderPage): Observable<Int> {
        return Observable.just(MangaPage.READY)
    }

}