package top.rechinx.meow.rikka.rx

import io.reactivex.Flowable
import io.reactivex.Observable
import io.reactivex.functions.BiFunction

/**
 * Returns a flowable that emits the current emission paired with the previous one.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Flowable<T>.scanWithPrevious(): Flowable<Pair<T, T?>> {
    return scan(Pair<T?, T?>(null, null)) { prev, newValue -> Pair(newValue, prev.first) }
            .skip(1) as Flowable<Pair<T, T?>>
}

/**
 * Returns an observable that emits the current emission paired with the previous one.
 */
@Suppress("UNCHECKED_CAST")
fun <T> Observable<T>.scanWithPrevious(): Observable<Pair<T, T?>> {
    return scan(Pair<T?, T?>(null, null)) { prev, newValue -> Pair(newValue, prev.first) }
            .skip(1) as Observable<Pair<T, T?>>
}

/**
 * Returns a flowable that skips the null values of the result of the given [block] function.
 */
inline fun <T, R> Flowable<T>.mapNullable(crossinline block: (T) -> R?): Flowable<R> {
    return flatMap { block(it)?.let { Flowable.just(it) } ?: Flowable.empty() }
}

fun <T, U, R> Flowable<T>.combineLatest(o2: Flowable<U>, combineFn: (T, U) -> R): Flowable<R> {
    return Flowable.combineLatest(this, o2, BiFunction<T, U, R>(combineFn))
}

typealias Dispatcher<T> = Pair<T, T?>