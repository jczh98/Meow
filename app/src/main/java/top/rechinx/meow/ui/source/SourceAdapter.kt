package top.rechinx.meow.ui.source

import android.content.Context
import android.graphics.Rect
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_source.view.*
import top.rechinx.meow.R
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.ui.base.BaseAdapter

class SourceAdapter(context: Context, list: ArrayList<Source>): BaseAdapter<Source>(context, list) {

    override fun getItemDecoration(): RecyclerView.ItemDecoration? {
        return object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
                val offset = parent.width / 90
                outRect.set(offset, 0, offset, (offset * 1.5).toInt())
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val source = datas[position]
        holder.itemView.item_source_title.text = source.name
        holder.itemView.item_source_switch.isChecked = true
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ViewHolder(inflater.inflate(R.layout.item_source, parent, false))
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)
}