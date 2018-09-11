package top.rechinx.meow.module.base

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import butterknife.ButterKnife
import top.rechinx.meow.model.Chapter

abstract class BaseAdapter<T>: RecyclerView.Adapter<RecyclerView.ViewHolder> {

    var mContext: Context
    var mData: ArrayList<T>
    var mInflater: LayoutInflater

    private var mClickListener: OnItemClickListener? = null
    private var mLongClickListener: OnItemLongClickListener? = null

    constructor(context: Context, list: ArrayList<T>) {
        this.mContext = context
        this.mData = list
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun add(data: T) {
        if (mData.add(data)) {
            notifyItemInserted(mData.size)
        }
    }

    fun add(location: Int, data: T) {
        mData.add(location, data)
        notifyItemInserted(location)
    }

    fun addAll(collection: Collection<T>) {
        addAll(mData.size, collection)
    }

    fun addAll(location: Int, collection: Collection<T>) {
        if (mData.addAll(location, collection)) {
            notifyItemRangeInserted(location, collection.size)
        }
    }

    fun remove(position: Int) {
        mData.removeAt(position)
        notifyItemRemoved(position)
    }

    fun removeAll(collection: Collection<T>) {
        mData.removeAll(collection)
        notifyDataSetChanged()
    }

    fun clear() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun getItem(position: Int): T = mData[position]

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        holder.itemView.setOnClickListener { v ->
            if (mClickListener != null) {
                mClickListener?.onItemClick(v, holder.adapterPosition)
            }
        }
        holder.itemView.setOnLongClickListener(View.OnLongClickListener { v ->
            if (mLongClickListener == null) {
                return@OnLongClickListener false
            }
            mLongClickListener?.onItemLongClick(v, holder.adapterPosition)
            true
        })
    }

    abstract fun getItemDecoration(): RecyclerView.ItemDecoration?

    fun setOnItemClickListener(onItemClickListener: OnItemClickListener) {
        mClickListener = onItemClickListener
    }

    fun setOnItemLongClickListener(onItemLongClickListener: OnItemLongClickListener) {
        mLongClickListener = onItemLongClickListener
    }

    interface OnItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    interface OnItemLongClickListener {
        fun onItemLongClick(view: View, position: Int)
    }

    open class BaseViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        init {
            ButterKnife.bind(this, view)
        }
    }

}