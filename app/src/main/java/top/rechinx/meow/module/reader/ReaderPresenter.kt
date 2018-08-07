package top.rechinx.meow.module.reader

import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.network.Api
import top.rechinx.meow.source.Dmzj

class ReaderPresenter: BasePresenter<ReaderView>() {

    private val LOAD_NULL = 0
    private val LOAD_INIT = 1
    private val LOAD_PREV = 2
    private val LOAD_NEXT = 3

    private var status = LOAD_INIT

    override fun initSubscription() {

    }

    override fun onViewAttach() {
    }

    fun loadInit(cid: String, chapter_id: String, array: Array<Chapter>) {
        for (item in array) {
            if(item.chapter_id == chapter_id) {
                images(Api.getChapterImage(Dmzj(), cid, chapter_id))
            }
        }
    }

    private fun images(observable: Observable<List<ImageUrl>>) {
        mCompositeDisposable.add(observable
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    when(status) {
                        LOAD_INIT -> {
                            mView?.onInitLoadSuccess(it)
                        }
                        LOAD_PREV -> {
                            mView?.onPrevLoadSuccess(it)
                        }
                        LOAD_NEXT -> {
                            mView?.onNextLoadSuccess(it)
                        }
                    }
                    status = LOAD_NULL
                },{
                    mView?.onParseError()
                }))
    }

}