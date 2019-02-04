package top.rechinx.meow.ui.library

import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import timber.log.Timber
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.data.AppDatabase
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.model.Manga

class LibraryAdapter(
        private val listener: Listener? = null
) : ListAdapter<Pair<Manga, Source>, MangaHolder>(Diff()){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MangaHolder {
        return MangaHolder(parent, this)
    }

    override fun onBindViewHolder(holder: MangaHolder, position: Int) {
        val item = getItem(position)
        holder.bind(item.first, item.second)
    }

    override fun onViewRecycled(holder: MangaHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }

    fun handleClick(adapterPosition: Int) {
        val item = getItem(adapterPosition)
        listener?.onMangaClick(item.first)
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

    private class Diff : DiffUtil.ItemCallback<Pair<Manga, Source>>() {

        override fun areItemsTheSame(oldItem: Pair<Manga, Source>, newItem: Pair<Manga, Source>): Boolean {
            return oldItem === newItem || oldItem.first.id == newItem.first.id
        }

        override fun areContentsTheSame(oldItem: Pair<Manga, Source>, newItem: Pair<Manga, Source>): Boolean {
            return oldItem == newItem
        }
    }
}