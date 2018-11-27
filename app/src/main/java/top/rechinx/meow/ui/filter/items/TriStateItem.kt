package top.rechinx.meow.ui.filter.items

import android.view.View
import android.widget.CheckedTextView
import androidx.recyclerview.widget.RecyclerView
import androidx.vectordrawable.graphics.drawable.VectorDrawableCompat
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import top.rechinx.meow.core.source.model.Filter
import top.rechinx.rikka.ext.dpToPx
import top.rechinx.rikka.ext.getResourceColor
import top.rechinx.meow.R as TR

open class TriStateItem(val filter: Filter.TriState) : AbstractFlexibleItem<TriStateItem.Holder>() {

    override fun getLayoutRes(): Int {
        return TR.layout.item_navigation_checkdtext
    }

    override fun getItemViewType(): Int {
        return 103
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): Holder? {
        return Holder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: Holder, position: Int, payloads: MutableList<Any>?) {
        val view = holder.text
        view.text = filter.name

        fun getIcon() = VectorDrawableCompat.create(view.resources, when (filter.state) {
            Filter.TriState.STATE_IGNORE -> TR.drawable.ic_check_box_outline_blank_24dp
            Filter.TriState.STATE_INCLUDE -> TR.drawable.ic_check_box_24dp
            Filter.TriState.STATE_EXCLUDE -> TR.drawable.ic_check_box_x_24dp
            else -> throw Exception("Unknown state")
        }, null)?.apply {
            val color = if (filter.state == Filter.TriState.STATE_INCLUDE)
                com.google.android.material.R.attr.colorAccent
            else
                android.R.attr.textColorSecondary

            setTint(view.context.getResourceColor(color))
        }

        view.setCompoundDrawablesWithIntrinsicBounds(getIcon(), null, null, null)
        holder.itemView.setOnClickListener {
            filter.state = (filter.state + 1) % 3
            view.setCompoundDrawablesWithIntrinsicBounds(getIcon(), null, null, null)
        }
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false
        return filter == (other as TriStateItem).filter
    }

    override fun hashCode(): Int {
        return filter.hashCode()
    }

    class Holder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter) {

        val text: CheckedTextView = itemView.findViewById(TR.id.naViewItem)

        init {
            // Align with native checkbox
            text.setPadding(4.dpToPx, 0, 0, 0)
            text.compoundDrawablePadding = 20.dpToPx
        }
    }

}