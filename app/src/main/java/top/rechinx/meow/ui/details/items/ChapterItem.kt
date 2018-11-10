package top.rechinx.meow.ui.details.items

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_chapter.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.ui.details.DetailAdapter

class ChapterItem(val chapter: Chapter): AbstractFlexibleItem<ChapterItem.ViewHolder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.bindTo(chapter)
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is ChapterItem) {
            return chapter.id == other.chapter.id
        }
        return false
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter as DetailAdapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_chapter

    class ViewHolder(private val view: View, private val adapter: DetailAdapter): FlexibleViewHolder(view, adapter) {
        fun bindTo(chapter: Chapter) {
            view.item_chapter_button.text = chapter.name
            view.item_chapter_button.isSelected = adapter.latestChapterId == chapter.id
        }
    }

}