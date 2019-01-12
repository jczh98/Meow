package top.rechinx.meow.ui.catalogbrowse

import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.rikka.rx.RxSchedulers
import javax.inject.Inject
import javax.inject.Provider

class CatalogBrowseViewModelFactory @Inject constructor(
        private val sourceManager: SourceManager,
        private val getMangasListFromSource: GetMangasListFromSource,
        private val schedulers: RxSchedulers
) {

    fun create(params: CatalogBrowseParams): CatalogBrowseViewModel {
        return CatalogBrowseViewModel(params, sourceManager, getMangasListFromSource, schedulers)
    }

}