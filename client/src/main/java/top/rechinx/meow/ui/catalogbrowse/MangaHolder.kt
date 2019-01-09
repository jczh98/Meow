package top.rechinx.meow.ui.catalogbrowse

import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_manga_grid.view.*
import top.rechinx.meow.R
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.rikka.ext.inflate

class MangaHolder(
        parent: ViewGroup
) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_manga_grid)) {

    fun bind(manga: Manga) {
        itemView.apply {
            title.text = manga.title

            GlideApp.with(this)
                    .load(manga)
                    .into(thumbnail)
        }
    }

    fun recycle() {
        GlideApp.with(itemView).clear(itemView.thumbnail)
    }
}