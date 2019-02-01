package top.rechinx.meow.rikka.livedata

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.snakydesign.livedataextensions.scan
import com.snakydesign.livedataextensions.skip

/**
 * Returns a [MutableLiveData] that emits the current emission paired with tht previous one
 */
@Suppress("UNCHECKED_CAST")
fun <T> MutableLiveData<T>.scanWithPrevious() : MutableLiveData<Pair<T, T?>> {
    return scan(Pair<T?, T?>(null, null)) { accumulatedValue, currentValue ->
        Pair(currentValue, accumulatedValue.first)
    }.skip(1) as MutableLiveData<Pair<T, T?>>
}