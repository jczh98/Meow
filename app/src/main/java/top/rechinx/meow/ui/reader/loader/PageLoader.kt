package top.rechinx.meow.ui.reader.loader

import androidx.annotation.CallSuper
import io.reactivex.Observable
import top.rechinx.meow.ui.reader.model.ReaderPage

abstract class PageLoader {

    var isRecycled = false
        private set

    @CallSuper
    open fun recycle() {
        isRecycled = false
    }

    abstract fun getPages(): Observable<List<ReaderPage>>

    abstract fun getPage(page: ReaderPage): Observable<Int>

    open fun retryPage(page: ReaderPage) {}
}