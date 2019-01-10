package top.rechinx.meow.ui.manga

import android.content.Context
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.jaredrummler.cyanea.Cyanea
import com.jaredrummler.cyanea.utils.ColorUtils
import kotlinx.android.synthetic.main.item_manga_info_header.view.*
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.MangaInfo
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.ext.gone
import java.text.DateFormat
import java.util.*


class MangaHeaderHolder(
        private val view: View,
        private val theme: Theme
) : RecyclerView.ViewHolder(view) {

    fun bind(manga: Manga) {
        itemView.apply {
            title.text = manga.title
            Glide.with(this).load(manga).into(cover)
            author.text = manga.author
            var statusString = ""
            when(manga.status) {
                MangaInfo.ONGOING -> statusString += context.getString(R.string.string_manga_status_ongoing)
                MangaInfo.COMPLETED -> statusString += context.getString(R.string.string_manga_status_completed)
                MangaInfo.UNKNOWN -> statusString += context.getString(R.string.string_manga_status_unknown)
            }
            status.text = statusString
            description.text = manga.description
            genres.setBorderColor(theme.tagColor)
            genres.setTextColor(theme.tagColor)
            if(manga.genres.isNotBlank()) {
                genres.setTags(manga.genres.split(", "))
            } else {
                genres.gone()
            }
            updated.text = if(manga.lastUpdate != 0L) {
                DateFormat.getDateInstance(DateFormat.SHORT).format(Date(manga.lastUpdate))
            } else {
                context.getString(R.string.string_manga_status_unknown)
            }
        }
    }

    fun recycle() {
        Glide.with(itemView).clear(itemView.cover)
    }

    class Theme(context: Context) {

        private val cyanea: Cyanea get() = Cyanea.instance

        val tagColor = if (ColorUtils.isLightColor(cyanea.primary, 1.0)) {
            ContextCompat.getColor(context, R.color.textColorPrimary)
        } else {
            cyanea.primary
        }

    }

}

