package top.rechinx.meow.support.mvp

interface BaseView<out T : BasePresenter<*>> {

    val presenter: T
}