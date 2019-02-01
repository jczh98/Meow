package top.rechinx.meow.ui.catalogbrowse.filters

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import top.rechinx.meow.R
import top.rechinx.meow.rikka.ext.inflate

class FiltersAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * The list of filters.
     */
    var items = emptyList<FilterWrapper<*>>()
        private set

    /**
     * Updates the adapter with given [filters] and notifies data set changed.
     */
    fun updateItems(filters: List<FilterWrapper<*>>) {
        items = filters
        notifyDataSetChanged()
    }

    /**
     * Returns the number of items.
     */
    override fun getItemCount(): Int {
        return items.size
    }

    /**
     * Returns the view type for the item with [position].
     */
    override fun getItemViewType(position: Int): Int {
        val filter = items[position]
        return when (filter) {
            is FilterWrapper.Select<*> -> SELECT_HOLDER
            is FilterWrapper.Text -> TEXT_HOLDER
            is FilterWrapper.Check -> CHECK_HOLDER
            is FilterWrapper.Group -> GROUP_HOLDER
            else -> 0
        }
    }

    /**
     * Creates a new view holder for the given [viewType].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            SELECT_HOLDER -> SelectHolder(parent.inflate(R.layout.item_navigation_spinner))
//            TEXT_HOLDER -> TextHolder(parent)
//            CHECK_HOLDER -> ChipHolder(parent)
//            GROUP_HOLDER -> GroupHolder(parent)
            else -> object : RecyclerView.ViewHolder(View(parent.context)) {} // TODO all types
        }
    }

    /**
     * Binds this [holder] with the item on this [position].
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = items[position]
        when (holder) {
            is SelectHolder -> holder.bind(item)
//            is ChipHolder -> holder.bind(item)
//            is GroupHolder -> holder.bind(item)
//            is TextHolder -> holder.bind(item)
        }
    }

    private companion object {

        /**
         * View type for a [FilterWrapper.Text]
         */
        const val TEXT_HOLDER = 1

        /**
         * View type for a [FilterWrapper.Check]
         */
        const val CHECK_HOLDER = 2

        /**
         * View type for a [FilterWrapper.Select]
         */
        const val SELECT_HOLDER = 3

        /**
         * View type for a [FilterWrapper.Group]
         */
        const val GROUP_HOLDER = 5
    }

}