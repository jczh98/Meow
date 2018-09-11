package top.rechinx.meow.module.detail

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.ButterKnife
import com.bumptech.glide.Glide
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import com.hippo.ripple.Ripple
import top.rechinx.meow.R
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.module.base.BaseAdapter
import top.rechinx.meow.module.reader.ReaderActivity
import top.rechinx.meow.utils.Utility
import top.rechinx.meow.widget.ChapterButton

class DetailAdapter: BaseAdapter<Chapter> {

    private var mComic: Comic? = null

    private var last: String? = null

    private lateinit var mClickCallback: OnClickCallback
    private lateinit var mFavoriteClickCallback: OnClickCallback

    constructor(context: Context, list: ArrayList<Chapter>): super(context, list)


    fun getFirst(): Chapter {
        return mData[mData.size - 1]
    }

    fun clearAll() {
        mData.clear()
        notifyDataSetChanged()
    }

    fun getDataSet(): ArrayList<Chapter> = mData

    fun setComic(comic: Comic) {
        this.mComic = comic
    }

    fun getComic(): Comic? = mComic

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return HeaderViewHolder(mInflater.inflate(R.layout.item_chapter_header, parent, false))
        }
        return ChapterViewHolder(mInflater.inflate(R.layout.item_chapter, parent, false))
    }

    override fun getItemCount(): Int = mData.size + 1

    override fun getItemViewType(position: Int): Int = if(position == 0) 0 else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if(position == 0) {
            var headerHolder = holder as HeaderViewHolder

            Ripple.addRipple(headerHolder.mRead, false)
            Ripple.addRipple(headerHolder.mFavorite, false)

            headerHolder.mRead.setOnClickListener {
                if(mClickCallback != null) {
                    mClickCallback.onClick(it, 1)
                }
            }
            headerHolder.mFavorite.setOnClickListener {
                if(mFavoriteClickCallback != null) {
                    mFavoriteClickCallback.onClick(it, 2)
                }
            }
            if(mComic != null) {
                Glide.with(mContext).load(mComic?.glideCover).into(headerHolder.mComicImage)
                headerHolder.mComicTitle.text = mComic?.title
                headerHolder.mComicIntro.text = mComic?.description
                headerHolder.mComicAuthor.text = mComic?.author
                headerHolder.mComicStatus.text = mComic?.status
                headerHolder.mComicUpdate.text = "最后更新： ${mComic?.update}"
                headerHolder.mRead.text = if(mComic?.last_chapter == null) "Read" else "Continue"
                headerHolder.mFavorite.text = if(mComic?.favorite == null) "Favorite" else "Subscribed"
            }
        } else {
            val chapter = mData[position - 1]
            var chapterHolder = holder as ChapterViewHolder
            chapterHolder.mChapterButton.text = chapter.title
        }
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return if (position == 0) manager.spanCount else 1
            }
        }
    }

    fun setOnClickCallback(onClickCallback: OnClickCallback) {
        mClickCallback = onClickCallback
    }

    fun setOnFavoriteClickCallback(onFavoriteClickCallback: OnClickCallback) {
        mFavoriteClickCallback = onFavoriteClickCallback
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State?) {
            val position = parent.getChildLayoutPosition(view)
            if (position == 0) {
                outRect.set(0, 0, 0, 10)
            } else {
                val offset = parent.width / 40
                outRect.set(offset, 0, offset, (offset * 1.5).toInt())
            }
        }
    }

    interface OnClickCallback {
        fun onClick(view: View, type: Int)
    }

    class HeaderViewHolder(itemView: View): BaseViewHolder(itemView) {
        @BindView(R.id.item_header_comic_image) lateinit var mComicImage: ImageView
        @BindView(R.id.item_header_comic_title) lateinit var mComicTitle: TextView
        @BindView(R.id.item_header_comic_intro) lateinit var mComicIntro: TextView
        @BindView(R.id.item_header_comic_status) lateinit var mComicStatus: TextView
        @BindView(R.id.item_header_comic_update) lateinit var mComicUpdate: TextView
        @BindView(R.id.item_header_comic_author) lateinit var mComicAuthor: TextView
        @BindView(R.id.favorite) lateinit var mFavorite: TextView
        @BindView(R.id.read) lateinit var mRead: TextView
    }

    class ChapterViewHolder(itemView: View): BaseViewHolder(itemView) {
        @BindView(R.id.item_chapter_button) lateinit var mChapterButton: ChapterButton
    }
}