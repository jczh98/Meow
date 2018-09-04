package top.rechinx.meow.module.detail

import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseView

interface DetailView: BaseView {

    fun onComicLoadSuccess(comic: Comic)

    fun onChapterLoadSuccess(list: List<Chapter>)

    fun onParseError()

    fun onLoadMoreSuccess(list: List<Chapter>)

    fun onLoadMoreFailure()

    fun onRefreshFinished()
}
