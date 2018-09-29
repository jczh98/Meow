package top.rechinx.meow.module.source

import android.content.Context
import io.reactivex.Observable
import io.reactivex.ObservableSource
import io.reactivex.ObservableTransformer
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.annotations.NonNull
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import top.rechinx.meow.App
import top.rechinx.meow.engine.SaSource
import top.rechinx.meow.manager.LoginManager
import top.rechinx.meow.manager.PreferenceManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.support.log.L
import top.rechinx.meow.utils.FileUtils
import java.io.File
import java.text.FieldPosition

class SourcePresenter(context: Context): BasePresenter<SourceView>() {

    private lateinit var mSourceManager: SourceManager
    private lateinit var mPreferences: PreferenceManager
    private lateinit var mLoginManager: LoginManager
    private var mContext = context

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        mLoginManager = LoginManager.getInstance()
        mPreferences = PreferenceManager(mContext)
    }

    fun load() {
        mCompositeDisposable.add(FileUtils.loadFiles(App.instance.getBasePath())
                .compose { upstream -> upstream.flatMap { ts -> Observable.fromIterable(ts) }.
                        map { file -> mSourceManager.getSource(file.name.replace(".xml", "")) }
                        .toList()
                        .toObservable() }
                .compose { upstream -> upstream.flatMap { ts -> Observable.fromIterable(ts) }.
                        map { source ->
                            if(source.isNeedLogin()) {
                                if(mLoginManager.isLogin(source.name)) {
                                    return@map Source(source.name, source.title, source.desc, mPreferences.getBoolean(source.name, false))
                                } else {
                                    return@map Source(source.name, source.title, source.desc, false)
                                }

                            } else {
                                return@map Source(source.name, source.title, source.desc, mPreferences.getBoolean(source.name, true))
                            }
                        }
                        .toList()
                        .toObservable() }
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    mView?.onSourceLoadSuccess(it)
                }, {
                    mView?.onSourceLoadFailure()
                })
        )
    }

    fun update(source: Source, position: Int) {
        var current = mSourceManager.getSource(source.name)
        if(current.isNeedLogin()) {
            if(!mLoginManager.isLogin(source.name)) {
                if(source.isEnable) mView?.doLogin(source.name, position)
            } else {
                mPreferences.putBoolean(source.name, source.isEnable)
            }
        } else {
            mPreferences.putBoolean(source.name, source.isEnable)
        }
    }
}