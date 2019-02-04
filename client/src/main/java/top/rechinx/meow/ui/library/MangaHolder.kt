package top.rechinx.meow.ui.library

import android.graphics.drawable.Drawable
import android.view.View
import android.view.ViewGroup
import android.view.ViewParent
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.palette.graphics.Palette
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import kotlinx.android.synthetic.main.item_manga_grid_styled.view.*
import timber.log.Timber
import top.rechinx.meow.R
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.rikka.ext.inflate

class MangaHolder(
        parent: ViewGroup,
        adapter: LibraryAdapter
) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_manga_grid_styled)) {

    init {
        itemView.setOnClickListener {
            adapter.handleClick(adapterPosition)
        }
    }

    fun bind(manga: Manga, source: Source) {
        itemView.apply {
            title.text = manga.title
            title_source.text = source.name
            GlideApp.with(this)
                    .load(manga)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(e: GlideException?, model: Any?, target: Target<Drawable>?, isFirstResource: Boolean): Boolean {
                            Timber.e(e, "Glide load error")
                            return false
                        }

                        override fun onResourceReady(resource: Drawable?, model: Any?, target: Target<Drawable>?, dataSource: DataSource?, isFirstResource: Boolean): Boolean {
                            val drawable = resource ?: return false
                            Palette.from(drawable.toBitmap()).generate {
                                constraint_layout.setBackgroundColor(it?.mutedSwatch?.rgb ?:
                                    ContextCompat.getColor(context, R.color.md_black_1000_12))
                            }
                            thumbnail.setImageDrawable(drawable)
                            return true
                        }

                    })
                    .into(thumbnail)
        }
    }

    fun recycle() {
        GlideApp.with(itemView).clear(itemView.thumbnail)
    }
}