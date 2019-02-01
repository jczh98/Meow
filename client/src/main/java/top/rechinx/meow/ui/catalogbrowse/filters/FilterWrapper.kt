package top.rechinx.meow.ui.catalogbrowse.filters

import top.rechinx.meow.core.source.model.Filter

/**
 * A wrapper for [Filter]
 * [updateInnerValue] should be called on each updated filter
 * [reset] used to be recover to the initial value
 */
sealed class FilterWrapper<T>(val filter: Filter<T>) {

    /**
     * The value of this wrapped filter. It's initially set to the value of the original filter.
     */
    var value = filter.value

    /**
     * Resets the value on the wrapped filter.
     */
    fun reset() {
        value = filter.initialValue
    }

    /**
     * Updates the value of the original filter with the one from this wrapper.
     */
    fun updateInnerValue() {
        filter.value = value
    }


    open class Text(filter: Filter.Text) : FilterWrapper<String>(filter)

    open class Select<V>(
            filter: Filter.Select<V>
    ) : FilterWrapper<Int>(filter)

    open class Group(filter: Filter.Group) : FilterWrapper<Unit>(filter)

    open class Sort(
            filter: Filter.Sort
    ) : FilterWrapper<Filter.Sort.Selection?>(filter)

    open class Check(
            filter: Filter.CheckBox
    ) : FilterWrapper<Boolean>(filter)

    companion object {
        fun from(filter: Filter<*>): FilterWrapper<*> {
            return when (filter) {
                is Filter.Text -> FilterWrapper.Text(filter)
                is Filter.Select<*> -> FilterWrapper.Select(filter)
                is Filter.Group -> FilterWrapper.Group(filter)
                is Filter.Sort -> FilterWrapper.Sort(filter)
                is Filter.CheckBox -> FilterWrapper.Check(filter)
            }
        }
    }
}