package top.rechinx.meow.ui.result

import android.content.Context
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import kotlinx.android.synthetic.main.item_result.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.glide.GlideApp

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

        itemHolder.itemView.apply {
            title.text = manga.title
            author.text = if(manga.author.isNullOrEmpty()) context.getString(R.string.unknown) else manga.author
            source.text = manga.sourceName
            GlideApp.with(context)
                    .load(manga)
                    .centerCrop()
                    .circleCrop()
                    .into(thumbnail)
        }
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    class ViewHolder(view: View): RecyclerView.ViewHolder(view)

}