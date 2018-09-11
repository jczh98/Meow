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
import top.rechinx.meow.module.base.BaseAdapter


class ReaderAdapter: BaseAdapter<ImageUrl> {

    private lateinit var mCallback: OnTouchCallback

    constructor(context: Context, list: ArrayList<ImageUrl>): super(context, list)

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

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val images = mData[position]
        val itemHolder = holder as ViewHolder
        itemHolder.mImage.setOnViewTapListener { view, x, y ->
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
        Glide.with(mContext).load(images.chapterUrl).into(itemHolder.mImage)
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    fun setOnTouchCallback(callback: OnTouchCallback) {
        mCallback = callback
    }

    interface OnTouchCallback {

        fun onCenter()

        fun onPrev()

        fun onNext()
    }

    class ViewHolder(view: View): BaseViewHolder(view) {
        @BindView(R.id.reader_image_view) lateinit var mImage: PhotoView
    }
}