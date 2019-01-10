package top.rechinx.meow.ui.manga

import android.content.Context
import android.graphics.Color
import android.view.View
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.jaredrummler.cyanea.Cyanea
import com.jaredrummler.cyanea.utils.ColorUtils
import kotlinx.android.synthetic.main.item_chapter.view.*
import top.rechinx.meow.R
import top.rechinx.meow.domain.chapter.model.Chapter

class ChapterHolder(
        view: View,
        val theme: Theme
        ) : RecyclerView.ViewHolder(view) {

    fun bind(chapter: Chapter) {
        itemView.apply {
            chapter_button.customTextColor = theme.normalColor
            chapter_button.primaryColor = theme.normalColor
            chapter_button.text = chapter.name
            chapter_button.isSelected = false
        }
    }

    class Theme(context: Context) {

        private val cyanea: Cyanea get() = Cyanea.instance

        val chapterPrimaryColor = if (ColorUtils.isLightColor(cyanea.primary, 1.0)) {
            Color.BLACK
        } else {
            cyanea.primary
        }

        val normalColor = if (cyanea.isDark) {
            ContextCompat.getColor(context, R.color.textColorPrimaryInverse)
        } else {
            if (ColorUtils.isLightColor(cyanea.primary, 1.0)) {
                Color.BLACK
            } else {
                cyanea.primary
            }
        }
    }

}

