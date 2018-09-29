package top.rechinx.meow.module.reader

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.manager.ComicManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BasePresenter
import io.reactivex.functions.Function
import top.rechinx.meow.manager.LoginManager

class ReaderPresenter: BasePresenter<ReaderView>() {

    private val LOAD_NULL = 0
    private val LOAD_INIT = 1
    private val LOAD_PREV = 2
    private val LOAD_NEXT = 3

    private var status = LOAD_INIT

    private lateinit var mChapterManger: ChapterManger
    private lateinit var mComic: Comic
    private lateinit var mSourceManger: SourceManager
    private lateinit var mComicManager: ComicManager
    private lateinit var mLoginManager: LoginManager

    override fun onViewAttach() {
        mSourceManger = SourceManager.getInstance()
        mComicManager = ComicManager.getInstance()
        mLoginManager = LoginManager.getInstance()
    }

    fun loadInit(source: String, cid: String, chapter_id: String, array: Array<Chapter>) {
        mComic = mComicManager.identify(source, cid)
        for (index in array.indices) {
            val item = array[index]
            if(item.chapter_id == chapter_id) {
                mChapterManger = ChapterManger(array, index)
                images(SourceManager.getInstance().rxGetSource(source)
                        .flatMap(Function<SaSource, Observable<List<ImageUrl>>> {
                            if(mLoginManager.isLogin(it.name)) it.setLogin(mLoginManager.getAuth(it.name))
                            return@Function it.getChapterImage(cid, chapter_id)
                        }))
            }
        }
    }

    fun loadPrev() {
        if(status == LOAD_NULL && mChapterManger.hasPrev()) {
            val chapter = mChapterManger.prevChapter
            status = LOAD_PREV
            images(SourceManager.getInstance().rxGetSource(mComic.source!!)
                    .flatMap(Function<SaSource, Observable<List<ImageUrl>>> {
                        if(mLoginManager.isLogin(it.name)) it.setLogin(mLoginManager.getAuth(it.name))
                        return@Function it.getChapterImage(mComic.cid!!, chapter?.chapter_id!!)
                    }))
            mView?.onPrevLoading()
        }else {
            mView?.onPrevLoadNone()
        }
    }

    fun loadNext() {
        if(status == LOAD_NULL && mChapterManger.hasNext()) {
            val chapter = mChapterManger.nextChapter
            status = LOAD_NEXT
            images(SourceManager.getInstance().rxGetSource(mComic.source!!)
                    .flatMap(Function<SaSource, Observable<List<ImageUrl>>> {
                        if(mLoginManager.isLogin(it.name)) it.setLogin(mLoginManager.getAuth(it.name))
                        return@Function it.getChapterImage(mComic.cid!!, chapter?.chapter_id!!)
                    }))
            mView?.onNextLoading()
        }else {
            mView?.onNextLoadNone()
        }
    }

    fun toNextChapter() {
        val chapter = mChapterManger.nextChapter()
        if(chapter != null) {
            updateChapter(chapter, true)
        }
    }

    fun toPrevChapter() {
        val chapter = mChapterManger.prevChapter()
        if(chapter != null) {
            updateChapter(chapter, false)
        }
    }

    fun updateLast(page: Int) {
        mComic.last_chapter = mChapterManger.current()
        mComic.last_page = page
        mComic.history = true
        mCompositeDisposable.add(mComicManager.updateOrInsert(mComic)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe())
    }

    private fun updateChapter(chapter: Chapter, isNext: Boolean) {
        mView?.onChapterChanged(chapter)
    }

    private fun images(observable: Observable<List<ImageUrl>>) {
        mCompositeDisposable.add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(status) {
                        LOAD_INIT -> {
                            var chapter = mChapterManger.moveNext()
                            chapter.count = it.size
                            mView?.onChapterChanged(chapter)
                            mView?.onInitLoadSuccess(it)
                        }
                        LOAD_PREV -> {
                            var chapter = mChapterManger.movePrev()
                            chapter.count = it.size
                            mView?.onPrevLoadSuccess(it)
                        }
                        LOAD_NEXT -> {
                            var chapter = mChapterManger.moveNext()
                            chapter.count = it.size
                            mView?.onNextLoadSuccess(it)
                        }
                    }
                    status = LOAD_NULL
                },{
                    mView?.onParseError()
                }))
    }

    private class ChapterManger {
        private var prev: Int = 0
        private var next: Int = 0
        private var array: Array<Chapter>
        private var index: Int = 0

        constructor(array: Array<Chapter>, index: Int) {
            this.array = array
            this.index = index
            prev = index + 1
            next = index
        }

        val prevChapter: Chapter?
            get() = if (prev < array.size) array[prev] else null

        val nextChapter: Chapter?
            get() = if (next >= 0) array[next] else null

        fun current(): String {
            return array[index].chapter_id!!
        }

        fun hasPrev(): Boolean {
            return prev < array.size
        }

        fun hasNext(): Boolean {
            return next >= 0
        }

        fun prevChapter(): Chapter? {
            return if (index + 1 < prev) {
                array[++index]
            } else null
        }

        fun nextChapter(): Chapter? {
            return if (index - 1 > next) {
                array[--index]
            } else null
        }

        fun movePrev(): Chapter {
            return array[prev++]
        }

        fun moveNext(): Chapter {
            return array[next--]
        }

    }

}