package top.rechinx.meow.ui.details

import android.content.Context
import android.graphics.Rect
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import com.google.android.material.button.MaterialButton
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
import top.rechinx.meow.widget.MaterialChapterButton
import java.text.DateFormat
import java.util.*

class DetailAdapter : BaseAdapter<Chapter> {

    constructor(context: Context, list: ArrayList<Chapter>) : super(context, list)

    var manga: Manga? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return ChapterViewHolder(inflater.inflate(R.layout.item_chapter, parent, false))
    }


    override fun getItemCount(): Int {
        return datas.size
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        super.onBindViewHolder(holder, position)
        val chapter = datas[position]
        var chapterHolder = holder as ChapterViewHolder
        chapterHolder.chapterButton.text = chapter.name
        chapterHolder.chapterButton.isSelected = manga?.last_read_chapter_id == chapter.id
    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val manager = recyclerView.layoutManager as GridLayoutManager
        manager.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
    }

    override fun getItemDecoration(): RecyclerView.ItemDecoration? = object : RecyclerView.ItemDecoration() {
        override fun getItemOffsets(outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State) {
            val position = parent.getChildLayoutPosition(view)
            if (position == 0) {
                outRect.set(0, 0, 0, 10)
            } else {
                val offset = parent.width / 40
                outRect.set(offset, 0, offset, (offset * 1.5).toInt())
            }
        }
    }

    class ChapterViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val chapterButton: MaterialChapterButton by bindView(R.id.item_chapter_button)
    }
}