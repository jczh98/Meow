package top.rechinx.meow.ui.reader.viewer.pager

import com.f2prateek.rx.preferences2.Preference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import top.rechinx.meow.data.preference.PreferenceHelper

class PagerConfig(private val viewer: PagerViewer,
                  private val preference: PreferenceHelper) {

    private val disposables = CompositeDisposable()

    var usePageTransitions = false
        private set

    var volumeKeysEnabled = false
        private set

    var volumeKeysInverted = false
        private set

    init {
        preference.pageTransitions()
                .register({ usePageTransitions = it })

        preference.enableVolumeKeys()
                .register({ volumeKeysEnabled = it })

        preference.readWithVolumeKeysInverted()
                .register({ volumeKeysInverted = it })
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
                .addTo(disposables)
    }

    fun clear() {
        disposables.clear()
    }
}