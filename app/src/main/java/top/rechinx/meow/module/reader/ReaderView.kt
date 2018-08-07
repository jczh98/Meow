package top.rechinx.meow.module.reader

import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.module.base.BaseView

interface ReaderView: BaseView {

    fun onInitLoadSuccess(it: List<ImageUrl>)

    fun onPrevLoadSuccess(it: List<ImageUrl>)

    fun onNextLoadSuccess(it: List<ImageUrl>)

    fun onParseError()

    fun onPrevLoading()

    fun onNextLoading()

    fun onPrevLoadNone()

    fun onNextLoadNone()

    fun onChapterChanged(chapter: Chapter)
}
