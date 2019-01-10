package top.rechinx.meow.ui.catalogs

import android.view.LayoutInflater
import android.view.ViewGroup
import me.drakeet.multitype.ItemViewBinder
import top.rechinx.meow.data.catalog.model.Catalog

class CatalogBinder(
        private val adapter: CatalogAdapter
) : ItemViewBinder<Catalog, CatalogHolder>() {

    override fun onCreateViewHolder(inflater: LayoutInflater, parent: ViewGroup): CatalogHolder {
        return CatalogHolder(parent, adapter)
    }

    override fun onBindViewHolder(holder: CatalogHolder, item: Catalog) {
        holder.bind(item)
    }

    override fun onViewRecycled(holder: CatalogHolder) {
        holder.recyle()
        super.onViewRecycled(holder)
    }

}