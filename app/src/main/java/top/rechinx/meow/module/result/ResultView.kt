package top.rechinx.meow.module.result

import com.scwang.smartrefresh.layout.api.RefreshLayout
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseView

interface ResultView: BaseView {

    fun onSearchError()

    fun onSearchSuccess(comic: Comic)

    fun onLoadMoreSuccess()

    fun onLoadMoreFailure()

}