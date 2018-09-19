package top.rechinx.meow.module.common

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import top.rechinx.meow.R
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseAdapter
import top.rechinx.meow.module.result.ResultAdapter
import top.rechinx.meow.engine.Helper

class GridAdapter: BaseAdapter<Comic> {

    constructor(context: Context, list: ArrayList<Comic>): super(context, list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_grid, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val comic = mData[position]
        val itemHolder = holder as ViewHolder

        itemHolder.comicTitle.text = comic.title
        val glideUrl = GlideUrl(comic.image, Helper.parseHeaders(comic.headers!!))
        itemHolder.comicSource.text = comic.sourceName
        Glide.with(mContext)
                .load(glideUrl)
                .into(itemHolder.comicImage)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    class ViewHolder(itemView: View): BaseAdapter.BaseViewHolder(itemView) {

        @BindView(R.id.item_grid_image) lateinit var comicImage: ImageView
        @BindView(R.id.item_grid_title) lateinit var comicTitle: TextView
        @BindView(R.id.item_grid_subtitle) lateinit var comicSource: TextView
        @BindView(R.id.item_grid_symbol) lateinit var comicHighlight: View

    }
}