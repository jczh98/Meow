package top.rechinx.meow.module.result

import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseView

interface ResultView: BaseView {

    fun onSearchError()

    fun onSearchSuccess(comic: Comic)
}