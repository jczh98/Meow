package top.rechinx.meow.core.source.model

sealed class Filter<T>(val name: String, var value: T) {

    /**
     * Basic Filters
     */

    open class Text(name: String, value: String) : Filter<String>(name, value)

    open class CheckBox(name: String, value: Boolean = false) : Filter<Boolean>(name, value)

    open class Select<V>(name: String, val options: Array<V>, value: Int = 0) : Filter<Int>(name, value)

    open class Group(name: String, val filters: List<Filter<*>>) : Filter<Unit>(name, Unit)

    open class Sort(
            name: String, val options: Array<String>, value: Selection? = null
    ) : Filter<Sort.Selection?>(name, value) {

        data class Selection(val index: Int, val ascending: Boolean)
    }
}