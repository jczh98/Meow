package top.rechinx.meow.domain.catalog.interactor

import io.reactivex.Flowable
import top.rechinx.meow.data.catalog.model.LocalCatalog
import top.rechinx.meow.domain.catalog.repository.CatalogRepository
import javax.inject.Inject

class GetLocalCatalogs @Inject constructor(
        private val catalogRepository: CatalogRepository
) {

    fun interact() = Flowable.defer {

        val catalogsFlow = catalogRepository.getInternalCatalog()

        catalogsFlow.map { catalog ->
            catalog.sortedBy { it.name }
        }
    }

}