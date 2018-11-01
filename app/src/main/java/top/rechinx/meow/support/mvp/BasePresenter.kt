package top.rechinx.meow.support.mvp

interface BasePresenter<V> {

    fun subscribe(view: V)

    fun unsubscribe()

    var view : V?
}