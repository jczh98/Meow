package top.rechinx.meow.ui.details.items

import android.content.Context
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_detail_header.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.details.DetailAdapter
import top.rechinx.meow.core.source.model.SManga
import top.rechinx.rikka.ext.gone
import java.text.DateFormat
import java.util.*
import top.rechinx.rikka.theme.utils.ThemeUtils

class HeaderItem(val manga: Manga, val context: Context) : AbstractFlexibleItem<HeaderItem.Holder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: Holder, position: Int, payloads: MutableList<Any>?) {
        holder.bind(manga, context)
    }

    override fun equals(other: Any?): Boolean {
        if(this === other) return true
        if(other is HeaderItem) {
            return manga.id == other.manga.id
        }
        return false
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): Holder {
        return Holder(view, adapter as DetailAdapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_detail_header

    class Holder(private val view: View, private val adapter: DetailAdapter) : FlexibleViewHolder(view, adapter) {

        init {
            view.apply {
                mangaGenresTags.setBorderColor(ThemeUtils.getColorById(context, R.color.theme_color_primary))
                mangaGenresTags.setTextColor(ThemeUtils.getColorById(context, R.color.theme_color_primary))
            }
        }

        fun bind(manga: Manga, context: Context) {
            view.apply {
                mangaInfoTitle.text = manga.title
                Glide.with(this).load(manga).into(mangaInfoCover)
                mangaInfoAuthor.text = manga.author
                var statusString = ""
                when(manga.status) {
                    SManga.ONGOING -> statusString += context.getString(R.string.string_manga_statu_ongoing)
                    SManga.COMPLETED -> statusString += context.getString(R.string.string_manga_statu_completed)
                    SManga.UNKNOWN -> statusString += context.getString(R.string.string_manga_statu_unknown)
                }
                mangaInfoStatus.text = statusString
                mangaInfoDescription.text = manga.description
                if(manga.genre != null && manga.genre!!.isNotBlank()) mangaGenresTags.setTags(manga.genre?.split(", "))
                else mangaGenresTags.gone()
                mangaInfoUpdated.text = if(manga.last_update != 0L) {
                    DateFormat.getDateInstance(DateFormat.SHORT).format(Date(manga.last_update))
                } else {
                    context.getString(R.string.unknown)
                }
            }
        }
    }
}