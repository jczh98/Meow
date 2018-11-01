package top.rechinx.meow.ui.reader.viewer.pager

import com.f2prateek.rx.preferences2.Preference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import org.koin.standalone.KoinComponent
import org.koin.standalone.inject
import top.rechinx.meow.support.preference.PreferenceHelper

class PagerConfig(private val viewer: PagerViewer) : KoinComponent {

    private val preference: PreferenceHelper by inject()
    private val disposable = CompositeDisposable()

    var usePageTransitions = false
        private set

    init {
        preference.pageTransitions()
                .register({usePageTransitions = it})
    }

    private fun <T> Preference<T>.register(
            valueAssignment: (T) -> Unit,
            onChanged: (T) -> Unit = {}
    ) {
        asObservable()
                .doOnNext(valueAssignment)
                .skip(1)
                .distinctUntilChanged()
                .doOnNext(onChanged)
                .subscribe()
                .addTo(disposable)
    }

    fun clear() {
        disposable.clear()
    }
}