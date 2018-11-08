package top.rechinx.meow.ui.filter.items

import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.View
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.FrameLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.AbstractFlexibleItem
import eu.davidea.flexibleadapter.items.IFlexible
import eu.davidea.viewholders.FlexibleViewHolder
import kotlinx.android.synthetic.main.item_grid_fit.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.widget.AutofitRecyclerView
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible

class CatalogueItem(val manga: Manga) :
        AbstractFlexibleItem<CatalogueItem.CatalogueGridHolder>() {

    override fun createViewHolder(view: View, adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>?): CatalogueGridHolder {
        val parent = adapter?.recyclerView as AutofitRecyclerView
        view.apply {
            card.layoutParams = FrameLayout.LayoutParams(
                    MATCH_PARENT, parent.itemWidth / 3 * 4)
            gradient.layoutParams = FrameLayout.LayoutParams(
                    MATCH_PARENT, parent.itemWidth / 3 * 4 / 2, Gravity.BOTTOM)
        }
        return CatalogueGridHolder(view, adapter)
    }

    override fun bindViewHolder(adapter: FlexibleAdapter<IFlexible<RecyclerView.ViewHolder>>, holder: CatalogueGridHolder, position: Int, payloads: MutableList<Any>) {
        holder.bindTo(manga)
    }

    override fun getLayoutRes(): Int {
        return R.layout.item_grid_fit
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other is CatalogueItem) {
            return manga.id!! == other.manga.id!!
        }
        return false
    }

    override fun hashCode(): Int {
        return manga.id!!.hashCode()
    }

    class CatalogueGridHolder(private val view: View, private val adapter: FlexibleAdapter<*>) :
            FlexibleViewHolder(view, adapter) {

        fun bindTo(manga: Manga) {
            itemView.title.text = manga.title
            GlideApp.with(view.context).clear(itemView.thumbnail)
            if (!manga.thumbnail_url.isNullOrEmpty()) {
                GlideApp.with(view.context)
                        .load(manga)
                        .diskCacheStrategy(DiskCacheStrategy.DATA)
                        .centerCrop()
                        .placeholder(android.R.color.transparent)
                        .into(object : ImageViewTarget<Drawable>(itemView.thumbnail) {
                            override fun setResource(resource: Drawable?) {
                                itemView.thumbnail.setImageDrawable(resource)
                            }

                            override fun onLoadStarted(placeholder: Drawable?) {
                                itemView.contentProgress.visible()
                                super.onLoadStarted(placeholder)
                            }

                            override fun onLoadCleared(placeholder: Drawable?) {
                                itemView.contentProgress.gone()
                                super.onLoadCleared(placeholder)
                            }

                            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                                itemView.contentProgress.gone()
                                super.onResourceReady(resource, transition)
                            }
                        })
            }
        }

    }

}