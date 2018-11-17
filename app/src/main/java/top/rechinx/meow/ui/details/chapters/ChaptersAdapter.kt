package top.rechinx.meow.ui.details.chapters

import android.content.Context
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_chapter.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.ui.base.BaseAdapter

class ChaptersAdapter(context: Context, list: ArrayList<Switcher<Chapter>>): BaseAdapter<ChaptersAdapter.Switcher<Chapter>>(context, list) {

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_chapter, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val item = datas[position]
        holder.itemView.apply {
            chapterButton.text = item.element.name
            if(item.element.download) {
                chapterButton.setDownload(true)
                chapterButton.isSelected = false
            } else {
                chapterButton.setDownload(false)
                chapterButton.isSelected = item.enable
            }
        }
    }


    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView)

    class Switcher<T>(var element: T, var enable: Boolean) {
        fun switchEnable() {
            enable = !enable
        }
    }
}