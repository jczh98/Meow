package top.rechinx.meow.domain.page.loader

import androidx.annotation.CallSuper
import io.reactivex.Observable
import top.rechinx.meow.domain.page.model.ReaderPage

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