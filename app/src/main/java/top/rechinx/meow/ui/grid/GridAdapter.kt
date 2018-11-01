package top.rechinx.meow.ui.grid

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseAdapter

class GridAdapter: BaseAdapter<Manga> {

    constructor(context: Context, list: ArrayList<Manga>): super(context, list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_grid, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val manga = datas[position]
        val itemHolder = holder as ViewHolder

        itemHolder.comicTitle.text = manga.title
        itemHolder.comicSource.text = manga.sourceName
        Glide.with(context)
                .load(manga)
                .into(itemHolder.comicImage)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        val comicImage: ImageView by bindView(R.id.item_grid_image)
        val comicTitle: TextView by bindView(R.id.item_grid_title)
        val comicSource: TextView by bindView(R.id.item_grid_subtitle)
        val comicHighlight: View by bindView(R.id.item_grid_symbol)

    }
}