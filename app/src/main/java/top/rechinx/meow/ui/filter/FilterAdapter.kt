package top.rechinx.meow.ui.filter

import android.content.Context
import android.graphics.drawable.Drawable
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.paging.PagedList
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.ImageViewTarget
import com.bumptech.glide.request.transition.Transition
import kotlinx.android.synthetic.main.item_grid.view.*
import kotlinx.android.synthetic.main.item_grid_fit.view.*
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.support.log.L
import top.rechinx.meow.ui.filter.FilterAdapter.ViewHolder.Companion.diffCallback
import top.rechinx.meow.widget.AutofitRecyclerView
import top.rechinx.rikka.ext.gone
import top.rechinx.rikka.ext.visible

class FilterAdapter(val context: Context) : PagedListAdapter<Manga, FilterAdapter.ViewHolder>(diffCallback) {

    private lateinit var recyclerView: AutofitRecyclerView

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_grid_fit, parent, false)
        return ViewHolder(view)
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView as AutofitRecyclerView
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val manga = getItem(position)
        holder.itemView.apply {
            card.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, recyclerView.itemWidth / 3 * 4)
            gradient.layoutParams = FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT, recyclerView.itemWidth / 3 * 4 / 2, Gravity.BOTTOM)
            holder.itemView.title.text = manga?.title
        }
        GlideApp.with(context).load(manga)
                .centerCrop()
                .into(object : ImageViewTarget<Drawable>(holder.itemView.thumbnail) {
            override fun setResource(resource: Drawable?) {
                holder.itemView.thumbnail.setImageDrawable(resource)
            }

            override fun onLoadStarted(placeholder: Drawable?) {
                holder.itemView.progress.visible()
                super.onLoadStarted(placeholder)
            }

            override fun onLoadCleared(placeholder: Drawable?) {
                holder.itemView.progress.gone()
                super.onLoadCleared(placeholder)
            }

            override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                holder.itemView.progress.gone()
                super.onResourceReady(resource, transition)
            }
        })
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        companion object {
            val diffCallback = object : DiffUtil.ItemCallback<Manga>() {
                override fun areItemsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                    return oldItem.cid == newItem.cid
                }

                override fun areContentsTheSame(oldItem: Manga, newItem: Manga): Boolean {
                    return oldItem == newItem
                }

            }
        }
    }
}