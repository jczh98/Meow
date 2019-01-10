package top.rechinx.meow.data.catalog

import io.reactivex.Flowable
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.data.catalog.model.InternalCatalog
import top.rechinx.meow.domain.catalog.repository.CatalogRepository


internal class CatalogRepositoryImpl(
        private val sourceManager: SourceManager
) : CatalogRepository {

    var internalCatalogs = emptyList<InternalCatalog>()
        private set

    init {
        // Initialized the internal catalogs
        internalCatalogs = sourceManager.getSources()
                .filterIsInstance<CatalogSource>()
                .map { InternalCatalog(it.name, "Internal Catalog", it) }
    }

    override fun getInternalCatalog(): Flowable<List<InternalCatalog>>
        = Flowable.just(internalCatalogs)

}