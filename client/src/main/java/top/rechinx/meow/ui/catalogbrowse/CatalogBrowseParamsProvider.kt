package top.rechinx.meow.ui.catalogbrowse

import javax.inject.Provider


class CatalogBrowseParamsProvider(
        private val sourceId: Long
) : Provider<CatalogBrowseParams> {

    override fun get(): CatalogBrowseParams {
        return CatalogBrowseParams(sourceId)
    }

}