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

class SourceAdapter: RecyclerView.Adapter<SourceAdapter.ViewHolder> {

    private var mContext: Context
    private var mData: ArrayList<Source>
    private var mInflater: LayoutInflater

    constructor(context: Context, list: ArrayList<Source>) {
        this.mContext = context
        this.mData = list
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun add(data: Source) {
        if (mData.add(data)) {
            notifyItemInserted(mData.size)
        }
    }

    fun add(location: Int, data: Source) {
        mData.add(location, data)
        notifyItemInserted(location)
    }

    fun addAll(collection: Collection<Source>) {
        addAll(mData.size, collection)
    }

    fun addAll(location: Int, collection: Collection<Source>) {
        if (mData.addAll(location, collection)) {
            notifyItemRangeInserted(location, location + collection.size)
        }
    }

    fun clearAll() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int) : Source {
        return mData[position]
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_source, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val source = mData[position]
        holder.sourceTitle.text = source.title
        holder.sourceSwitch.isChecked = true
    }

    fun getItemDecoration(): RecyclerView.ItemDecoration {
        return object : RecyclerView.ItemDecoration() {
            override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
                val offset = parent.width / 90
                outRect.set(offset, 0, offset, (offset * 1.5).toInt())
            }
        }
    }

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {

        @BindView(R.id.item_source_switch) lateinit var sourceSwitch: SwitchCompat
        @BindView(R.id.item_source_title) lateinit var sourceTitle: TextView

        init {
            ButterKnife.bind(this, itemView)
        }
    }
}