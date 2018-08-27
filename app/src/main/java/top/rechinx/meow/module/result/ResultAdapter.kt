package top.rechinx.meow.module.result

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
import top.rechinx.meow.model.Comic
import android.icu.lang.UCharacter.GraphemeClusterBreak.T
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import top.rechinx.meow.App
import top.rechinx.meow.manager.SourceManager


class ResultAdapter: RecyclerView.Adapter<ResultAdapter.ViewHolder> {

    private var mContext: Context
    private var mData: ArrayList<Comic>
    private var mInflater: LayoutInflater

    private lateinit var mClickListener: OnItemClickListener

    constructor(context: Context, list: ArrayList<Comic>) {
        this.mContext = context
        this.mData = list
        this.mInflater = LayoutInflater.from(mContext)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_result, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val comic = mData[position]

        holder.itemView.setOnClickListener { v ->
            if (mClickListener != null) {
                mClickListener.onItemClick(v, holder.adapterPosition)
            }
        }

        holder.comicTitle.text = comic.title
        holder.comicAuthor.text = comic.author
        holder.comicUpdate.text = comic.update
        holder.comicSource.text = comic.source?.let { SourceManager.getInstance().getTitle(it) }
        Glide.with(mContext).load(comic.glideCover).into(holder.comicImage)
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

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mClickListener = onItemClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        @BindView(R.id.result_comic_image)
        lateinit var comicImage: ImageView
        @BindView(R.id.result_comic_title)
        lateinit var comicTitle: TextView
        @BindView(R.id.result_comic_author)
        lateinit var comicAuthor: TextView
        @BindView(R.id.result_comic_update)
        lateinit var comicUpdate: TextView
        @BindView(R.id.result_comic_source)
        lateinit var comicSource: TextView

        init {
            ButterKnife.bind(this, view)
        }
    }

}