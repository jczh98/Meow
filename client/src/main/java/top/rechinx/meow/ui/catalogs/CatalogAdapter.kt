package top.rechinx.meow.ui.catalogs

import androidx.fragment.app.Fragment
import me.drakeet.multitype.MultiTypeAdapter
import top.rechinx.meow.data.catalog.model.Catalog
import java.text.FieldPosition

class CatalogAdapter(
        catalogsFragment: CatalogsFragment
): MultiTypeAdapter() {

    val listener: Listener = catalogsFragment

    fun handleClick(position: Int) {
        val item = items[position] as? Catalog ?: return
        listener.onCatalogClick(item)
    }

    interface Listener {
        fun onCatalogClick(catalog: Catalog)
    }
}