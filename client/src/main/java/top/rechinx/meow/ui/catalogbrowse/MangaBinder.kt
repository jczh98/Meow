package top.rechinx.meow.ui.catalogbrowse

import android.view.LayoutInflater
import android.view.ViewGroup
import me.drakeet.multitype.ItemViewBinder
import top.rechinx.meow.domain.manga.model.Manga

class MangaBinder : ItemViewBinder<Manga, MangaHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): MangaHolder {
        return MangaHolder(parent)
    }

    override fun onBindViewHolder(holder: MangaHolder, item: Manga) {
        holder.bind(item)
    }

    override fun onViewRecycled(holder: MangaHolder) {
        holder.recycle()
        super.onViewRecycled(holder)
    }
}