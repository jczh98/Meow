package top.rechinx.meow.ui.catalogbrowse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.rxkotlin.addTo
import me.drakeet.multitype.Items
import timber.log.Timber
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel

class CatalogBrowseViewModel(
        private val sourceId: Long,
        private val sourceManager: SourceManager,
        private val getMangasListFromSource: GetMangasListFromSource,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    val mangaListLiveData : MutableLiveData<Resource<PagedList<Manga>>> = MutableLiveData()

    val mangaList: ArrayList<Manga> = ArrayList()

    var page = 1

    fun loadMore() {
        val source = sourceManager.getOrStub(sourceId) as CatalogSource
        mangaListLiveData.postValue(Resource.Loading())
        getMangasListFromSource.interact(source, page++)
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    mangaListLiveData.postValue(Resource.Success(it))
                }, {
                    mangaListLiveData.postValue(Resource.Error(it.message))
                })
                .addTo(disposables)
    }
}
