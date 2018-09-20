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
import top.rechinx.meow.manager.PreferenceManager
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BasePresenter
import top.rechinx.meow.utils.FileUtils
import java.io.File

class SourcePresenter(context: Context): BasePresenter<SourceView>() {

    private lateinit var mSourceManager: SourceManager
    private lateinit var mPreferences: PreferenceManager
    private var mContext = context

    override fun initSubscription() {
    }

    override fun onViewAttach() {
        mSourceManager = SourceManager.getInstance()
        mPreferences = PreferenceManager(mContext)
    }

    fun load() {
        val list = ArrayList<Source>()
        mCompositeDisposable.add(FileUtils.loadFiles(App.instance.getBasePath())
                .compose { upstream -> upstream.flatMap { ts -> Observable.fromIterable(ts) }.
                        map { file -> mSourceManager.getSource(file.name.replace(".xml", "")) }
                        .toList()
                        .toObservable() }
                .compose { upstream -> upstream.flatMap { ts -> Observable.fromIterable(ts) }.
                        map { source -> Source(source.name, source.title, source.desc, mPreferences.getBoolean(source.name, true)) }
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

    fun update(source: Source) {
        mPreferences.putBoolean(source.name, source.isEnable)
    }
}