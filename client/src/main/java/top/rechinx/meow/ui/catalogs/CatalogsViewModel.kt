package top.rechinx.meow.ui.catalogs

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Flowable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import top.rechinx.meow.data.catalog.model.Catalog
import top.rechinx.meow.data.catalog.model.InternalCatalog
import top.rechinx.meow.domain.catalog.interactor.GetLocalCatalogs
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel

class CatalogsViewModel(
        private val getLocalCatalogs: GetLocalCatalogs,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    private val localCatalogsLiveData: MutableLiveData<Resource<List<Catalog>>> = MutableLiveData()

    init {
        fetchDatas().addTo(disposables)
    }

    fun getLocalCatalogs() = localCatalogsLiveData

    private fun fetchDatas(): Disposable {
        localCatalogsLiveData.postValue(Resource.Loading())
        return getLocalCatalogs.interact()
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.io)
                .subscribe({
                    localCatalogsLiveData.postValue(Resource.Success(it))
                }, {
                    localCatalogsLiveData.postValue(Resource.Error(it.message))
                })
    }

}
