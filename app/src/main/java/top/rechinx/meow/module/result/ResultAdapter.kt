package top.rechinx.meow.module.result

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import com.bumptech.glide.Glide
import top.rechinx.meow.R
import top.rechinx.meow.model.Comic
import com.bumptech.glide.load.model.GlideUrl
import top.rechinx.meow.engine.Helper
import top.rechinx.meow.module.base.BaseAdapter


class ResultAdapter: BaseAdapter<Comic> {


    constructor(context: Context, list: ArrayList<Comic>): super(context, list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_result, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val comic = mData[position]
        val itemHolder = holder as ViewHolder

        itemHolder.comicTitle.text = comic.title
        itemHolder.comicAuthor.text = comic.author
        itemHolder.comicUpdate.text = comic.update
        itemHolder.comicSource.text = comic.sourceName
        var glideUrl = GlideUrl(comic.image, Helper.parseHeaders(comic.headers))
        Glide.with(mContext).load(glideUrl).into(itemHolder.comicImage)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    class ViewHolder(view: View): BaseViewHolder(view) {

        @BindView(R.id.result_comic_image) lateinit var comicImage: ImageView
        @BindView(R.id.result_comic_title) lateinit var comicTitle: TextView
        @BindView(R.id.result_comic_author) lateinit var comicAuthor: TextView
        @BindView(R.id.result_comic_update) lateinit var comicUpdate: TextView
        @BindView(R.id.result_comic_source) lateinit var comicSource: TextView
    }

}