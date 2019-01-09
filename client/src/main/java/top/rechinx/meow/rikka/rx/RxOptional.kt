package top.rechinx.meow.rikka.rx

/**
 * A wrapper around a nullable object because in RxJava2 emitting null is forbidden.
 */
sealed class RxOptional<out T> {

    /**
     * Represents an existing object, accesible through [value].
     */
    class Some<out T>(val value: T) : RxOptional<T>()

    /**
     * Represents an absent (null) object.
     */
    object None : RxOptional<Nothing>()

    /**
     * Returns the value or null.
     */
    fun get() = (this as? Some)?.value

    companion object {
        /**
         * Returns a [Some] if the given [value] is not null, otherwise [None].
         */
        fun <T> of(value: T?): RxOptional<T> {
            return if (value != null) Some(value) else None
        }
    }
}