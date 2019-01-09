package top.rechinx.meow.ui.catalogbrowse

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import timber.log.Timber
import top.rechinx.meow.core.source.CatalogSource
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.core.source.model.PagedList
import top.rechinx.meow.domain.manga.interactor.GetMangasListFromSource
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.rx.RxSchedulers

class CatalogBrowseViewModel(
        private val sourceManager: SourceManager,
        private val getMangasListFromSource: GetMangasListFromSource,
        private val schedulers: RxSchedulers
) : ViewModel() {

    val mangaListLiveData : MutableLiveData<Resource<List<Manga>>> = MutableLiveData()

    fun fetchMangaList(sourceId: Long) {
        val source = sourceManager.getOrStub(sourceId) as CatalogSource
        mangaListLiveData.postValue(Resource.Loading())
        val getMangaDisposable = getMangasListFromSource.interact(source, 1)
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    mangaListLiveData.postValue(Resource.Success(it.list))
                }, {
                    mangaListLiveData.postValue(Resource.Error(it.message))
                })
    }
}
