package top.rechinx.meow.module.reader

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.github.chrisbanes.photoview.OnViewTapListener
import com.github.chrisbanes.photoview.PhotoView
import top.rechinx.meow.R
import top.rechinx.meow.model.ImageUrl
import android.graphics.RectF



class ReaderAdapter: RecyclerView.Adapter<ReaderAdapter.ViewHolder> {

    private var mContext: Context
    private var mData: ArrayList<ImageUrl>
    private var mInflater: LayoutInflater

    private lateinit var mCallback: OnTouchCallback

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

    fun getItem(position: Int): ImageUrl = mData[position]

    fun getPositionByNum(current: Int, num: Int, reverse: Boolean): Int {
        var current = current
        while (mData[current].page_number !== num) {
            current = if (reverse) current - 1 else current + 1
        }
        return current
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(mInflater.inflate(R.layout.item_picture, parent, false))
    }

    override fun getItemCount(): Int = mData.size

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val images = mData[position]
        holder.mImage.setOnViewTapListener { view, x, y ->
            run {
                val mViewWidth = view.width
                val mViewHeight = view.height
                val mCenterRect = RectF(mViewWidth.toFloat() / 3, mViewHeight.toFloat() / 3,
                        mViewWidth.toFloat() * 2 / 3, mViewHeight.toFloat() * 2 / 3)
                val mLeftRect1 = RectF(0F, 0F,
                        mViewWidth.toFloat() * 2 / 3, mViewHeight.toFloat() / 3)
                val mLeftRect2 = RectF(0F, mViewHeight.toFloat() / 3,
                        mViewWidth.toFloat() / 3, mViewHeight.toFloat() * 2 / 3)
                val mRightRect1 = RectF(mViewWidth.toFloat() * 2 / 3, 0F,
                        mViewWidth.toFloat(), mViewHeight.toFloat() * 2 / 3)
                val mRightRect2 = RectF(mViewWidth.toFloat() / 3, mViewHeight.toFloat() * 2 / 3,
                        mViewWidth.toFloat(), mViewHeight.toFloat())
                if(mCenterRect.contains(x, y)) {
                    if(mCallback != null) {
                        mCallback.onCenter()
                    }
                }
                if(mLeftRect1.contains(x, y) || mLeftRect2.contains(x, y)) {
                    if(mCallback != null) {
                        mCallback.onPrev()
                    }
                }
                if(mRightRect1.contains(x, y) || mRightRect2.contains(x, y)) {
                    if(mCallback != null) {
                        mCallback.onNext()
                    }
                }
            }
        }
        Glide.with(mContext).load(images.chapterUrl).into(holder.mImage)
    }

    fun setOnTouchCallback(callback: OnTouchCallback) {
        mCallback = callback
    }

    interface OnTouchCallback {

        fun onCenter()

        fun onPrev()

        fun onNext()
    }

    class ViewHolder(view: View): RecyclerView.ViewHolder(view) {
        @BindView(R.id.reader_image_view) lateinit var mImage: PhotoView

        init {
            ButterKnife.bind(this, view)
        }
    }
}