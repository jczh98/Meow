package top.rechinx.meow.ui.catalogbrowse

import toothpick.config.Module
import top.rechinx.meow.di.bindInstance
import top.rechinx.meow.di.bindProviderInstance

class CatalogBrowseModule(catalogBrowseFragment: CatalogBrowseFragment) : Module() {

    init {
        bindProviderInstance(CatalogBrowseParamsProvider(catalogBrowseFragment.sourceId))
    }
}