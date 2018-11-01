package top.rechinx.meow.ui.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View

abstract class BaseAdapter<T>: RecyclerView.Adapter<RecyclerView.ViewHolder> {

    protected var context: Context
    protected var datas: ArrayList<T>
    protected var inflater: LayoutInflater

    private var clickListener: OnItemClickListener? = null
    private var longClickListener: OnItemLongClickListener? = null

    constructor(context: Context, list: ArrayList<T>) {
        this.context = context
        this.datas = list
        this.inflater = LayoutInflater.from(this.context)
    }

    fun add(data: T) {
        if (datas.add(data)) {
            notifyItemInserted(datas.size)
        }
    }

    fun add(location: Int, data: T) {
        datas.add(location, data)
        notifyItemInserted(location)
    }

    fun addAll(collection: Collection<T>) {
        addAll(datas.size, collection)
    }

    fun addAll(location: Int, collection: Collection<T>) {
        if (datas.addAll(location, collection)) {
            notifyItemRangeInserted(location, collection.size)
        }
    }

    fun remove(position: Int) {
        datas.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAll(collection: Collection<T>) {
        datas.removeAll(collection)
        notifyDataSetChanged()
    }

    fun clear() {
        datas.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T = datas[position]

    override fun getItemCount(): Int = datas.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            if (clickListener != null) {
                clickListener?.onItemClick(v, holder.adapterPosition)
            }
        }
        holder.itemView.setOnLongClickListener(View.OnLongClickListener { v ->
            if (longClickListener == null) {
                return@OnLongClickListener false
            }
            longClickListener?.onItemLongClick(v, holder.adapterPosition)
            true
        })
    }

    abstract fun getItemDecoration(): RecyclerView.ItemDecoration?

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        clickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        longClickListener = onItemLongClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

}