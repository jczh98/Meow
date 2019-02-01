package top.rechinx.meow.domain.page.model

import com.jakewharton.rxrelay2.BehaviorRelay
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.page.loader.PageLoader

data class ReaderChapter(val chapter: Chapter) {

    var state: State =
            State.Wait
        set(value) {
            field = value
            stateRelay.accept(value)
        }

    private val stateRelay by lazy { BehaviorRelay.createDefault(state) }

    val stateObserver by lazy { stateRelay }

    val pages: List<ReaderPage>?
        get() = (state as? State.Loaded)?.pages

    var pageLoader: PageLoader? = null

    var requestedPage: Int = 0

    var references = 0
        private set

    fun ref() {
        references++
    }

    fun unref() {
        references--
        if (references == 0) {
            pageLoader?.recycle()
            pageLoader = null
            state = State.Wait
        }
    }

    sealed class State {
        object Wait : State()
        object Loading : State()
        class Error(val error: Throwable) : State()
        class Loaded(val pages: List<ReaderPage>) : State()
    }
}