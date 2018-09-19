package top.rechinx.meow.engine

import android.app.Application
import com.eclipsesource.v8.V8
import com.eclipsesource.v8.V8Array
import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.Scheduler
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class JsEngine {

    private lateinit var engine: V8

    constructor(app: Application) {
        Observable.just(1)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {engine = V8.createV8Runtime(null, app.applicationInfo.dataDir)}
    }

    fun loadJs(funcs: String) {
        Observable.just(funcs)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {engine.executeScript(it)}
    }

    fun rxCallJs(func: String, vararg args: String): Observable<String> {
        return Observable.create(ObservableOnSubscribe<String> {
            try {
                val params = V8Array(engine)
                for(p in args) {
                    params.push(p)
                }
                val json = engine.executeStringFunction(func, params)
                it.onNext(json)
                it.onComplete()
            } catch (e: Exception) {
                e.printStackTrace()
                it.onError(e)
            }
        }).subscribeOn(AndroidSchedulers.mainThread())
                .observeOn(Schedulers.io())

    }
}