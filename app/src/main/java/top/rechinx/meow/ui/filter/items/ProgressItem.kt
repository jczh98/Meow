package top.rechinx.meow.ui.filter.items

import android.view.View
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_progress.view.*
import top.rechinx.meow.R


class ProgressItem : AbstractFlexibleItem<ProgressItem.ViewHolder>() {

    private var loadMore = true

    override fun getLayoutRes(): Int {
        return R.layout.item_progress
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder? {
        return ViewHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: ViewHolder, position: Int, payloads: MutableList<Any>) {
        holder.itemView.progressBar.visibility = View.GONE
        holder.itemView.progressMessage.visibility = View.GONE

        if (!adapter.isEndlessScrollEnabled) {
            loadMore = false
        }

        if (loadMore) {
            holder.itemView.progressBar.visibility = View.VISIBLE
        } else {
            holder.itemView.progressMessage.visibility = View.VISIBLE
        }
    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    class ViewHolder(view: View, adapter: FlexibleAdapter<*>) : FlexibleViewHolder(view, adapter)

}