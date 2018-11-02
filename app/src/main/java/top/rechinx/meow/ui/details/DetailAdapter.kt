package top.rechinx.meow.ui.details

import android.content.Context
import android.graphics.Rect
import android.support.v7.widget.GridLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.hippo.ripple.Ripple
import me.gujun.android.taggroup.TagGroup
import top.rechinx.meow.R
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.viewbinding.bindView
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.widget.ChapterButton
import java.text.DateFormat
import java.util.*

class DetailAdapter: BaseAdapter<Chapter> {

    constructor(context: Context, list: ArrayList<Chapter>): super(context, list)

    private lateinit var clickCallback: OnClickCallback
    private lateinit var favoriteClickCallback: OnClickCallback

    var manga: Manga? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 0) {
            return HeaderViewHolder(inflater.inflate(R.layout.item_chapter_header, parent, false))
        }
        return ChapterViewHolder(inflater.inflate(R.layout.item_chapter, parent, false))
    }

    override fun getItemCount(): Int  = datas.size + 1

    override fun getItemViewType(position: Int): Int = if(position == 0) 0 else 1

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)

        if(position == 0) {
            var headerHolder = holder as HeaderViewHolder

            Ripple.addRipple(headerHolder.read, false)
            Ripple.addRipple(headerHolder.favorite, false)

            headerHolder.read.setOnClickListener {
                if(clickCallback != null) {
                    clickCallback.onClick(it, 1)
                }
            }
            headerHolder.favorite.setOnClickListener {
                if(favoriteClickCallback != null) {
                    favoriteClickCallback.onClick(it, 2)
                }
            }
            if(manga != null) {
                Glide.with(context).load(manga).into(headerHolder.comicImage)
                // tab layout
                if (manga?.genre.isNullOrBlank().not()) {
                    L.d(manga?.genre)
                    headerHolder.genres.setTags(manga?.genre?.split(", "))
                }
                headerHolder.comicTitle.text = manga?.title
                headerHolder.comicIntro.text = manga?.description
                headerHolder.comicAuthor.text = manga?.author
                when(manga?.status) {
                    AbsManga.ONGOING -> headerHolder.comicStatus.text = context.getString(R.string.string_manga_statu_ongoing)
                    AbsManga.COMPLETED -> headerHolder.comicStatus.text = context.getString(R.string.string_manga_statu_completed)
                    AbsManga.UNKNOWN -> headerHolder.comicStatus.text = context.getString(R.string.string_manga_statu_unknown)
                }
                if(manga?.last_update != 0L) {
                    headerHolder.comicUpdate.text = "最后更新： ${DateFormat.getDateInstance(DateFormat.SHORT).format(Date(manga?.last_update!!))}"
                } else {
                    headerHolder.comicUpdate.text = "最后更新： ${context.getString(R.string.unknown)}"
                }

                headerHolder.read.text = if(manga?.last_read_chapter_id == -1L) context.getString(R.string.details_read) else context.getString(R.string.details_continue)
                headerHolder.favorite.text = if(manga?.favorite == false) context.getString(R.string.details_favorite) else context.getString(R.string.details_unfavorite)
            }
        } else {
            val chapter = datas[position - 1]
            var chapterHolder = holder as ChapterViewHolder
            chapterHolder.chapterButton.text = chapter.name
            chapterHolder.chapterButton.isSelected = manga?.last_read_chapter_id == chapter.id
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

    fun setOnClickCallback(onClickCallback: OnClickCallback) {
        clickCallback = onClickCallback
    }

    fun setOnFavoriteClickCallback(onFavoriteClickCallback: OnClickCallback) {
        favoriteClickCallback = onFavoriteClickCallback
    }

    interface OnClickCallback {
        fun onClick(view: View, type: Int)
    }

    class HeaderViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val comicImage: ImageView by bindView(R.id.item_header_comic_image)
        val comicTitle: TextView by bindView(R.id.item_header_comic_title)
        val comicIntro: TextView by bindView(R.id.item_header_comic_intro)
        val comicStatus: TextView by bindView(R.id.item_header_comic_status)
        val comicUpdate: TextView by bindView(R.id.item_header_comic_update)
        val comicAuthor: TextView by bindView(R.id.item_header_comic_author)
        val favorite: TextView by bindView(R.id.favorite)
        val read: TextView by bindView(R.id.read)
        val genres: TagGroup by bindView(R.id.mangaGenresTags)
    }

    class ChapterViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val chapterButton: ChapterButton by bindView(R.id.item_chapter_button)
    }
}