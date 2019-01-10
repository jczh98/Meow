package top.rechinx.meow.ui.catalogbrowse

import android.view.ViewGroup
import androidx.recyclerview.widget.*
import top.rechinx.meow.domain.manga.model.Manga

/**
 * Adapter for the list of manga from the catalogue. It can receive a [listener] as parameter to
 * handle clicks.
 */
class CatalogBrowseAdapter(
        private val listener: Listener? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    /**
     * Differ to dispatch list updates to the adapter.
     */
    private val differ = AsyncListDiffer(AdapterListUpdateCallback(this),
            AsyncDifferConfig.Builder(ItemDiff()).build())

    /**
     * Returns the number of items in the adapter.
     */
    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    /**
     * Returns the item at the given [position] or throws [IndexOutOfBoundsException].
     */
    fun getItem(position: Int): Any {
        return differ.currentList[position]
    }

    /**
     * Returns the item at the given [position] or null.
     */
    fun getItemOrNull(position: Int): Any? {
        return differ.currentList.getOrNull(position)
    }

    /**
     * Submits an update on the items. It receives a list of [mangas], whether [isLoading] more
     * results and [endReached].
     */
    fun submitList(mangas: List<Manga>, isLoading: Boolean, endReached: Boolean) {
        val items = mutableListOf<Any>()
        items.addAll(mangas)

        if (isLoading && mangas.isNotEmpty()) {
            items += LoadingMore
        } else if (endReached) {
            items += EndReached
        }

        differ.submitList(items)
    }

    /**
     * Returns the view type for the item on this [position].
     */
    override fun getItemViewType(position: Int): Int {
        return when (getItem(position)) {
            is Manga -> MANGA_VIEWTYPE
            else -> FOOTER_VIEWTYPE
        }
    }

    /**
     * Creates a new view holder for the given [viewType].
     */
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            MANGA_VIEWTYPE -> {
                MangaHolder(parent, this)
            }
            FOOTER_VIEWTYPE -> {
                FooterHolder(parent)
            }
            else -> error("$viewType is not a valid viewtype")
        }

    }

    /**
     * Binds this [holder] with the item on this [position].
     */
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (holder) {
            is MangaHolder -> {
                val manga = getItem(position) as Manga
                holder.bind(manga)
            }
            is FooterHolder -> {
                val item = getItem(position)
                holder.bind(item === LoadingMore, item === EndReached)
            }
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is MangaHolder -> holder.recycle()
        }
    }

    /**
     * Handles a user click on the element at the given [position]. The click is delegated to the
     * [listener] of this adapter.
     */
    fun handleClick(position: Int) {
        val manga = getItemOrNull(position) as? Manga ?: return
        listener?.onMangaClick(manga)
    }

    /**
     * Returns the span size for the item at the given [position]. Only used when in grid mode.
     */
    fun getSpanSize(position: Int): Int? {
        return when (getItemViewType(position)) {
            MANGA_VIEWTYPE -> 1
            else -> null
        }
    }

    /**
     * Listener used to delegate clicks on this adapter.
     */
    interface Listener {

        /**
         * Called when this [manga] was clicked.
         */
        fun onMangaClick(manga: Manga)

    }

    /**
     * Diff implementation for all the items on this adapter.
     */
    private class ItemDiff : DiffUtil.ItemCallback<Any>() {

        /**
         * Returns whether [oldItem] and [newItem] are the same.
         */
        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem === newItem -> true
                oldItem is Manga && newItem is Manga -> oldItem.id == newItem.id
                else -> false
            }
        }

        /**
         * Returns whether the contents of [oldItem] and [newItem] are the same.
         */
        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return oldItem == newItem
        }

    }

    /**
     * The item used in the adapter to show a progress bar.
     */
    private object LoadingMore

    /**
     * The item used in the adapter to display a message when there are no more results.
     */
    private object EndReached


    private companion object {
        /**
         * View type for a [Manga].
         */
        const val MANGA_VIEWTYPE = 1

        /**
         * View type for a [LoadingMore] or [EndReached] item.
         */
        const val FOOTER_VIEWTYPE = 2
    }

}