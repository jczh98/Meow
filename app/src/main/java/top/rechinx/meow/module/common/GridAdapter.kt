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
import top.rechinx.meow.R
import top.rechinx.meow.manager.SourceManager
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.result.ResultAdapter

class GridAdapter: RecyclerView.Adapter<GridAdapter.ViewHolder> {

    private var mContext: Context
    private var mData: ArrayList<Comic>
    private var mInflater: LayoutInflater

    private lateinit var mClickListener: GridAdapter.OnItemClickListener

    constructor(context: Context, list: ArrayList<Comic>) {
        this.mContext = context
        this.mData = list
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun add(data: Comic) {
        if (mData.add(data)) {
            notifyItemInserted(mData.size)
        }
    }

    fun add(location: Int, data: Comic) {
        mData.add(location, data)
        notifyItemInserted(location)
    }

    fun addAll(collection: Collection<Comic>) {
        addAll(mData.size, collection)
    }

    fun addAll(location: Int, collection: Collection<Comic>) {
        if (mData.addAll(location, collection)) {
            notifyItemRangeInserted(location, location + collection.size)
        }
    }

    fun clearAll() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int) : Comic {
        return mData[position]
    }

    fun setOnItemClickListener(onItemClickListener: GridAdapter.OnItemClickListener) {
        mClickListener = onItemClickListener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_grid, parent, false))
    }

    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = mData[position]

        holder.itemView.setOnClickListener { v ->
            if (mClickListener != null) {
                mClickListener.onItemClick(v, holder.adapterPosition)
            }
        }

        holder.comicTitle.text = comic.title
        holder.comicSource.text = SourceManager.getInstance().getTitle(comic.source!!)
        Glide.with(mContext)
                .load(SourceManager.getInstance().getParser(comic.source!!)?.constructCoverGlideUrl(comic.image!!))
                .into(holder.comicImage)
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        @BindView(R.id.item_grid_image) lateinit var comicImage: ImageView
        @BindView(R.id.item_grid_title) lateinit var comicTitle: TextView
        @BindView(R.id.item_grid_subtitle) lateinit var comicSource: TextView
        @BindView(R.id.item_grid_symbol) lateinit var comicHighlight: View

        init {
            ButterKnife.bind(this, view)
        }
    }
}