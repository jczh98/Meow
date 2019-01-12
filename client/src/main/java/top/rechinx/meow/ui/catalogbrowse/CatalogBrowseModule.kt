package top.rechinx.meow.ui.catalogbrowse

import toothpick.config.Module

class CatalogBrowseModule(catalogBrowseFragment: CatalogBrowseFragment) : Module() {

    init {
        bind(CatalogBrowseViewModelFactory::class.java).singletonInScope()
    }
}