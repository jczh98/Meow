package top.rechinx.meow.module.source

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.SwitchCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import top.rechinx.meow.R
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.Source
import top.rechinx.meow.module.base.BaseAdapter

class SourceAdapter: BaseAdapter<Source> {

    constructor(context: Context, list: ArrayList<Source>): super(context, list)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_source, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val source = mData[position]
        val itemHolder = holder as ViewHolder
        itemHolder.sourceTitle.text = source.title
        itemHolder.sourceSwitch.isChecked = true
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration {
        return object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                val offset = parent.width / 90
                outRect.set(offset, 0, offset, (offset * 1.5).toInt())
            }
        }
    }

    class ViewHolder(itemView: View): BaseViewHolder(itemView) {

        @BindView(R.id.item_source_switch) lateinit var sourceSwitch: SwitchCompat
        @BindView(R.id.item_source_title) lateinit var sourceTitle: TextView

    }
}