package top.rechinx.meow.ui.reader.viewer.webtoon

import com.f2prateek.rx.preferences2.Preference
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.rxkotlin.addTo
import top.rechinx.meow.data.preference.PreferenceHelper

class WebtoonConfig(private val preference: PreferenceHelper) {

    private val disposables = CompositeDisposable()

    var volumeKeysEnabled = false
        private set

    var volumeKeysInverted = false
        private set

    init {
        preference.enableVolumeKeys()
                .register({ volumeKeysEnabled = it })

        preference.readWithVolumeKeysInverted()
                .register({ volumeKeysInverted = it })
    }

    fun unsubscribe() {
        disposables.clear()
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
}