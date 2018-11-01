package top.rechinx.meow.core.source.model

sealed class Filter<T>(val name: String, var state: T) {
    open class Header(name: String): Filter<Any>(name, 0)
    abstract class Select<V>(name: String, val values: Array<V>, state: Int = 0): Filter<Int>(name, state)
    abstract class Text(name: String, state: String = "") : Filter<String>(name, state)
    abstract class Group<V>(name: String, state: List<V>): Filter<List<V>>(name, state)
}