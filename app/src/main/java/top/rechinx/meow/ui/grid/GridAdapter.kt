package top.rechinx.meow.ui.grid

import android.content.Context
import android.view.Gravity
import androidx.recyclerview.widget.RecyclerView
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.bumptech.glide.Glide
import eu.davidea.flexibleadapter.FlexibleAdapter
import eu.davidea.flexibleadapter.items.IFlexible
import kotlinx.android.synthetic.main.item_grid_fit.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.ui.base.BaseAdapter
import top.rechinx.meow.glide.GlideApp
import top.rechinx.meow.support.log.L
import top.rechinx.meow.ui.grid.items.GridItem
import top.rechinx.meow.widget.AutofitRecyclerView
import top.rechinx.rikka.ext.visible

class GridAdapter(context: Context): FlexibleAdapter<GridItem>(null, context, true) {

    fun removeItemByMangaId(mangaId: Long) {
        for (position in 0 until itemCount) {
            val item = getItem(position)?.manga ?: continue
            if(item.id == mangaId) {
                removeItem(position)
                break
            }
        }
    }
}