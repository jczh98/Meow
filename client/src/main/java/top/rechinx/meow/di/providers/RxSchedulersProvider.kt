package top.rechinx.meow.di.providers

import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import toothpick.ProvidesSingletonInScope
import top.rechinx.meow.rikka.rx.RxSchedulers
import javax.inject.Provider
import javax.inject.Singleton


@Singleton
@ProvidesSingletonInScope
internal class RxSchedulersProvider : Provider<RxSchedulers> {

    override fun get(): RxSchedulers {
        return RxSchedulers(
                io = Schedulers.io(),
                computation = Schedulers.computation(),
                main = AndroidSchedulers.mainThread()
        )
    }

}