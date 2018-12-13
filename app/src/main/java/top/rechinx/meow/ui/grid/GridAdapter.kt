package top.rechinx.meow.ui.grid

import android.content.Context
import eu.davidea.flexibleadapter.FlexibleAdapter
import top.rechinx.meow.ui.grid.items.GridItem

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