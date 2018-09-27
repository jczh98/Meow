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
import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.integration.okhttp3.OkHttpUrlLoader
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import top.rechinx.meow.App
import top.rechinx.meow.engine.Helper
import top.rechinx.meow.engine.HttpDns
import top.rechinx.meow.module.base.BaseAdapter
import java.io.InputStream
import java.net.InetAddress


class ReaderAdapter: BaseAdapter<ImageUrl> {

    private lateinit var mCallback: OnTouchCallback

    private var mMode: Int = 0
    private lateinit var mTapListener: OnViewTapListener

    constructor(context: Context, list: ArrayList<ImageUrl>): super(context, list)

    fun getPositionByNum(current: Int, num: Int, reverse: Boolean): Int {
        var current = current
        while (mData[current].page_number !== num) {
            current = if (reverse) current - 1 else current + 1
        }
        return current
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if(mMode == PAGE_READER_MODE)
            PageViewHolder(mInflater.inflate(R.layout.item_picture, parent, false))
        else
            StreamViewHolder(mInflater.inflate(R.layout.item_picture_stream, parent, false))
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        val images = mData[position]

        if(mMode == PAGE_READER_MODE) {
            val itemHolder = holder as PageViewHolder
            itemHolder.mImage.setOnViewTapListener(mTapListener)
            val glideUrl = GlideUrl(images.imageUrl, Helper.parseHeaders(images.headers))
            Glide.with(mContext).load(glideUrl).into(itemHolder.mImage)
        } else {
            val itemHolder = holder as StreamViewHolder
            val glideUrl = GlideUrl(images.imageUrl, Helper.parseHeaders(images.headers))
            Glide.with(mContext).load(glideUrl).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    itemHolder.mImage.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                    itemHolder.mImage.setImageDrawable(resource)
                }
            })
        }

    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = null

    fun setReaderMode(mode: Int) {
        this.mMode = mode
    }

    fun setOnTouchCallback(callback: OnTouchCallback) {
        mCallback = callback
    }

    fun setOnViewTapListener(listener: OnViewTapListener) {
        this.mTapListener = listener
    }

    interface OnTouchCallback {

        fun onCenter()

        fun onPrev()

        fun onNext()
    }

    class PageViewHolder(view: View): BaseViewHolder(view) {
        @BindView(R.id.reader_image_view) lateinit var mImage: PhotoView
    }

    class StreamViewHolder(view: View): BaseViewHolder(view) {
        @BindView(R.id.reader_image_view) lateinit var mImage: ImageView
    }

    companion object {

        const val PAGE_READER_MODE: Int = 0
        const val STREAM_READER_MODE: Int = 1

    }
}