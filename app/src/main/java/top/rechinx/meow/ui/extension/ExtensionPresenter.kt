package top.rechinx.meow.ui.extension

import android.os.Bundle
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.core.extension.ExtensionManager
import top.rechinx.meow.support.log.L
import top.rechinx.rikka.mvp.BasePresenter

class ExtensionPresenter: BasePresenter<ExtensionFragment>(), KoinComponent {

    private val extensionManager: ExtensionManager by inject()

    override fun onCreate(savedState: Bundle?) {
        super.onCreate(savedState)

        extensionManager.installedExtensionsRelay
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeLatestCache({ view, items ->
                    view.setInstalledExtensions(items)
                })
    }
}