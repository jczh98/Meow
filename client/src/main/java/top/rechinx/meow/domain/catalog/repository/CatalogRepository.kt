package top.rechinx.meow.domain.catalog.repository

import io.reactivex.Flowable
import top.rechinx.meow.data.catalog.model.InternalCatalog

interface CatalogRepository {

    /**
     * Interface of getting internal catalog
     */
    fun getInternalCatalog() : Flowable<List<InternalCatalog>>
}