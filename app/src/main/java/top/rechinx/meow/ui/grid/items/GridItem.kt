package top.rechinx.meow.ui.grid.items

import android.view.Gravity
import android.view.View
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_grid_fit.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.widget.AutofitRecyclerView
import top.rechinx.rikka.ext.visible

class GridItem(val manga: Manga) : AbstractFlexibleItem<GridItem.ViewHolder>() {

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?, holder: ViewHolder, position: Int, payloads: MutableList<Any>?) {
        holder.bind(manga)
    }

    override fun equals(other: Any?): Boolean {
        return manga.id == (other as GridItem).manga.id
    }

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>): ViewHolder {
        return ViewHolder(view, adapter)
    }

    override fun getLayoutRes(): Int = R.layout.item_grid_fit

    class ViewHolder(private val view: View, private val adapter: FlexibleAdapter<*>): FlexibleViewHolder(view, adapter) {

        fun bind(manga: Manga) {
            val recyclerView = adapter.recyclerView as AutofitRecyclerView
            view.apply {
                card.layoutParams = FrameLayout.LayoutParams(
                        recyclerView.itemWidth, recyclerView.itemWidth / 3 * 4)
                gradient.layoutParams = FrameLayout.LayoutParams(
                        recyclerView.itemWidth, recyclerView.itemWidth / 3 * 4 / 2, Gravity.BOTTOM)
                title.text = manga.title
                subTitle.visible()
                subTitle.text = manga.sourceName
                GlideApp.with(context)
                        .load(manga)
                        .centerCrop()
                        .into(thumbnail)
            }
        }
    }
}