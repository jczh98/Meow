package top.rechinx.meow.ui.catalogs

import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.item_catalog.view.*
import top.rechinx.meow.R
import top.rechinx.meow.data.catalog.model.Catalog
import top.rechinx.meow.rikka.ext.inflate
import top.rechinx.meow.rikka.ext.visibleIf
import top.rechinx.meow.glide.GlideApp

class CatalogHolder(
        parent: ViewGroup,
        adapter: CatalogAdapter
) : RecyclerView.ViewHolder(parent.inflate(R.layout.item_catalog)) {

    init {
        itemView.setOnClickListener {
            adapter.handleClick(adapterPosition)
        }
    }

    fun bind(catalog: Catalog) {
        itemView.apply {
            title.text = catalog.name
            description.text = catalog.description
            description.visibleIf { catalog.description.isNotEmpty() }
            GlideApp.with(this)
                    .load(catalog)
                    .into(image)
        }
    }

    fun recyle() {
        itemView.apply {
            GlideApp.with(this).clear(image)
        }
    }
}