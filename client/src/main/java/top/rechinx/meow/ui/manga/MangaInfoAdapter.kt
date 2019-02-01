package top.rechinx.meow.ui.manga

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import top.rechinx.meow.R
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.ext.inflate

class MangaInfoAdapter(
        private val manga: Manga,
        private val listener: Listener? = null
) : ListAdapter<Any, RecyclerView.ViewHolder>(Diff()) {

    init {
        submitList(listOf(manga))
    }

    override fun getItemViewType(position: Int): Int {
        val item = getItem(position)
        return when (item) {
            is Manga -> VIEWTYPE_HEADER
            is Chapter -> VIEWTYPE_CHAPTER
            else -> error("Unknown view type for item class ${item.javaClass}")
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType) {
            VIEWTYPE_HEADER -> MangaHeaderHolder(
                    parent.inflate(R.layout.item_manga_info_header),
                    MangaHeaderHolder.Theme(parent.context)
            )
            VIEWTYPE_CHAPTER -> ChapterHolder(
                    parent.inflate(R.layout.item_chapter),
                    this,
                    ChapterHolder.Theme(parent.context)
            )
            else -> error("Unknown view holder for view type $viewType")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val item = getItem(position)
        return when (holder) {
            is MangaHeaderHolder -> holder.bind(item as Manga)
            is ChapterHolder -> holder.bind(item as Chapter)
            else -> error((" Unknown view holder for item class ${item.javaClass}"))
        }
    }

    override fun onViewRecycled(holder: RecyclerView.ViewHolder) {
        when (holder) {
            is MangaHeaderHolder -> holder.recycle()
        }
    }

    fun getSpanSize(position: Int): Int? {
        return when (getItemViewType(position)) {
            VIEWTYPE_CHAPTER -> 1
            else -> null
        }
    }

    /**
     * Handles a user click on the element at the given [position]. The click is delegated to the
     * [listener] of this adapter.
     */
    fun handleClick(position: Int) {
        val item = getItem(position)
        when (item) {
            is Manga -> {}
            is Chapter -> listener?.onChapterClick(item)
            else -> error("Unknown item type of this $position")
        }
    }


    private class Diff : DiffUtil.ItemCallback<Any>() {

        override fun areItemsTheSame(oldItem: Any, newItem: Any): Boolean {
            return when {
                oldItem is Manga && newItem is Manga -> true
                oldItem is Chapter && newItem is Chapter -> true
                else -> oldItem == newItem
            }
        }

        override fun areContentsTheSame(oldItem: Any, newItem: Any): Boolean {
            return true
        }
    }

    /**
     * Listener used to delegate clicks on this adapter.
     */
    interface Listener {

        /**
         * Called when this [chapter] was clicked.
         */
        fun onChapterClick(chapter: Chapter)

    }

    companion object {

        /**
         * View type for manga header
         */
        const val VIEWTYPE_HEADER = 0

        /**
         * View type for [Chapter]
         */
        const val VIEWTYPE_CHAPTER = 1
    }
}