package top.rechinx.meow.module.source

import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BaseView

interface SourceView: BaseView {

    fun onSourceLoadSuccess(list: List<Source>)

    fun onSourceLoadFailure()

    fun onLoginFailured(position: Int)

    fun doLogin(name: String, position: Int)
}