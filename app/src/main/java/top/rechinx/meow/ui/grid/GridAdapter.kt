package top.rechinx.meow.ui.grid

import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import kotlinx.android.synthetic.main.item_grid_fit.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.support.log.L
import top.rechinx.meow.widget.AutofitRecyclerView
import top.rechinx.rikka.ext.visible

class GridAdapter: BaseAdapter<Manga> {

    constructor(context: Context, list: ArrayList<Manga>): super(context, list)

    private lateinit var recyclerView: AutofitRecyclerView

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        this.recyclerView = recyclerView as AutofitRecyclerView
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val view = inflater.inflate(R.layout.item_grid_fit, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val manga = datas[position]
        val itemHolder = holder as ViewHolder

        itemHolder.itemView.apply {
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

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)
}