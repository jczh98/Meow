package top.rechinx.meow.ui.details.items

import android.view.View
import android.view.ViewGroup
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.widget.Button
import android.widget.FrameLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_chapter.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.log.L
import top.rechinx.meow.widget.AutofitRecyclerView

class ChapterItem(val chapter: Chapter, val manga: Manga): AbstractFlexibleItem<ChapterItem.ViewHolder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.bindTo(chapter, manga)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is ChapterItem) {
            return chapter.id == other.chapter.id
        }
        return false
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_chapter

    class ViewHolder(private val view: View, private val adapter: FlexibleAdapter<*>): FlexibleViewHolder(view, adapter) {
        fun bindTo(chapter: Chapter, manga: Manga) {
            view.item_chapter_button.text = chapter.name
            view.item_chapter_button.isSelected = manga.last_read_chapter_id == chapter.id
        }
    }

}