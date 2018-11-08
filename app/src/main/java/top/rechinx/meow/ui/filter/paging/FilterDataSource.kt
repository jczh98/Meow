package top.rechinx.meow.ui.filter.paging

import androidx.lifecycle.MutableLiveData
import androidx.paging.ItemKeyedDataSource
import io.reactivex.disposables.CompositeDisposable
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.AbsManga
import top.rechinx.meow.core.source.model.FilterList
import top.rechinx.meow.data.database.model.Manga

class FilterDataSource(private val source: Source,
                       private val query: String,
                       private val filterList: FilterList): ItemKeyedDataSource<Int, Manga>() {

    val networkState: MutableLiveData<NetworkState> = MutableLiveData()

    private val compositeDisposable: CompositeDisposable = CompositeDisposable()

    private var pageNumber = 1

    fun clear() {
        pageNumber = 1
        compositeDisposable.clear()
    }

    override fun loadInitial(params: LoadInitialParams<Int>, callback: LoadInitialCallback<Manga>) {
        networkState.postValue(NetworkState.Loading())

        if(query.isBlank()&&filterList.isEmpty()) {
            val mangaListObservable = source.fetchPopularManga(pageNumber)
                    .subscribe({
                        val list = it.map { item ->
                            val manga = Manga()
                            manga.copyFrom(item)
                            manga.sourceId = source.id
                            manga.sourceName = source.name
                            manga
                        }
                        pageNumber++
                        callback.onResult(list)
                        networkState.postValue(NetworkState.Loaded())
                    }, {
                        networkState.postValue(NetworkState.Error(it.message))
                    })
            compositeDisposable.add(mangaListObservable)
        } else {
            val mangaListObservable = source.fetchSearchManga(query, pageNumber, filterList)
                    .subscribe({
                        val list = it.map { item ->
                            val manga = Manga()
                            manga.copyFrom(item)
                            manga.sourceId = source.id
                            manga.sourceName = source.name
                            manga
                        }
                        pageNumber++
                        callback.onResult(list)
                        networkState.postValue(NetworkState.Loaded())
                    }, {
                        networkState.postValue(NetworkState.Error(it.message))
                    })
            compositeDisposable.add(mangaListObservable)
        }
    }

    override fun loadAfter(params: LoadParams<Int>, callback: LoadCallback<Manga>) {
        networkState.postValue(NetworkState.Loading())

        if(query.isBlank()&&filterList.isEmpty()) {
            val mangaListObservable = source.fetchPopularManga(pageNumber)
                    .subscribe({
                        val list = it.map { item ->
                            val manga = Manga()
                            manga.copyFrom(item)
                            manga.sourceId = source.id
                            manga.sourceName = source.name
                            manga
                        }
                        pageNumber++
                        callback.onResult(list)
                        networkState.postValue(NetworkState.Loaded())
                    }, {
                        networkState.postValue(NetworkState.Error(it.message))
                    })
            compositeDisposable.add(mangaListObservable)
        } else {
            val mangaListObservable = source.fetchSearchManga(query, pageNumber, filterList)
                    .subscribe({
                        val list = it.map { item ->
                            val manga = Manga()
                            manga.copyFrom(item)
                            manga.sourceId = source.id
                            manga.sourceName = source.name
                            manga
                        }
                        pageNumber++
                        callback.onResult(list)
                        networkState.postValue(NetworkState.Loaded())
                    }, {
                        networkState.postValue(NetworkState.Error(it.message))
                    })
            compositeDisposable.add(mangaListObservable)
        }
    }

    override fun loadBefore(params: LoadParams<Int>, callback: LoadCallback<Manga>) {
    }

    override fun getKey(item: Manga): Int {
        return pageNumber
    }

}