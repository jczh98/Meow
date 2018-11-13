package top.rechinx.meow.ui.details.items

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_chapter_loadmore.view.*
import top.rechinx.meow.R
import top.rechinx.meow.ui.details.DetailAdapter
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible


class LoadItem: AbstractFlexibleItem<LoadItem.ViewHolder>() {

    private var holder: LoadItem.ViewHolder? = null

    var status = IDLE

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: LoadItem.ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.itemView.apply {
            if (status == IDLE) {
                loadButton.visible()
                loadButton.setOnClickListener { (adapter as DetailAdapter).onLoadMoreListener.onLoadMore() }
                itemProgressLoad.gone()
            } else if (status == LOADING) {
                itemProgressLoad.visible()
                loadButton.gone()
            }
        }

    }

    override fun equals(other: Any?): Boolean {
        return this === other
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        holder = ViewHolder(view, adapter as DetailAdapter)
        return holder!!
    }

    override fun getLayoutRes(): Int = R.layout.item_chapter_loadmore

    class ViewHolder(view: View, adapter: DetailAdapter): FlexibleViewHolder(view, adapter)

    companion object {
        const val IDLE = 0
        const val LOADING = 1
    }
}