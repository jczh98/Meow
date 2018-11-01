package top.rechinx.meow.ui.result

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseAdapter

class ResultAdapter: BaseAdapter<Manga> {


    constructor(context: Context, list: ArrayList<Manga>): super(context, list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_result, parent, false))
    }

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val manga = datas[position]
        val itemHolder = holder as ViewHolder

        itemHolder.comicTitle.text = manga.title
        itemHolder.comicAuthor.text = manga.author
        itemHolder.comicUpdate.text = ""
        itemHolder.comicSource.text = manga.sourceName
        Glide.with(context).load(manga).into(itemHolder.comicImage)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {

        val comicImage: ImageView by bindView(R.id.result_comic_image)
        val comicTitle: TextView by bindView(R.id.result_comic_title)
        val comicAuthor: TextView by bindView(R.id.result_comic_author)
        val comicUpdate: TextView by bindView(R.id.result_comic_update)
        val comicSource: TextView by bindView(R.id.result_comic_source)
    }

}