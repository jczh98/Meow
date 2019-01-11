package top.rechinx.meow.ui.catalogbrowse

import toothpick.config.Module
import top.rechinx.meow.di.bindInstance

class CatalogBrowseModule(catalogBrowseFragment: CatalogBrowseFragment) : Module() {

    init {
        val params = CatalogBrowseParams().also {
            it.sourceId = catalogBrowseFragment.sourceId
        }
        bindInstance(params)
    }
}