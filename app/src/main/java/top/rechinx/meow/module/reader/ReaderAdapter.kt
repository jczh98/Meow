package top.rechinx.meow.module.reader

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.github.chrisbanes.photoview.PhotoView
import top.rechinx.meow.R
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl

class ReaderAdapter: RecyclerView.Adapter<ReaderAdapter.ViewHolder> {

    private var mContext: Context
    private var mData: ArrayList<ImageUrl>
    private var mInflater: LayoutInflater

    constructor(context: Context, list: ArrayList<ImageUrl>) {
        this.mContext = context
        this.mData = list
        this.mInflater = LayoutInflater.from(mContext)
    }

    fun add(data: ImageUrl) {
        if (mData.add(data)) {
            notifyItemInserted(mData.size)
        }
    }

    fun add(location: Int, data: ImageUrl) {
        mData.add(location, data)
        notifyItemInserted(location)
    }

    fun addAll(collection: Collection<ImageUrl>) {
        addAll(mData.size, collection)
    }

    fun addAll(location: Int, collection: Collection<ImageUrl>) {
        if (mData.addAll(location, collection)) {
            notifyItemRangeInserted(location, location + collection.size)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_picture, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        @BindView(R.id.reader_image_view) lateinit var mImage: PhotoView

        init {
            ButterKnife.bind(this, view)
        }
    }
}