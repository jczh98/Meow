package top.rechinx.meow.ui.manga

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import io.reactivex.Single
import io.reactivex.rxkotlin.addTo
import timber.log.Timber
import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.chapter.interactor.FetchChaptersFromSource
import top.rechinx.meow.domain.chapter.model.Chapter
import top.rechinx.meow.domain.manga.interactor.GetManga
import top.rechinx.meow.domain.manga.interactor.UpdateManga
import top.rechinx.meow.domain.manga.model.Manga
import top.rechinx.meow.rikka.misc.Resource
import top.rechinx.meow.rikka.rx.RxSchedulers
import top.rechinx.meow.rikka.rx.RxViewModel
import javax.inject.Inject

class MangaInfoViewModel @Inject constructor(
        private val getManga: GetManga,
        private val updateManga: UpdateManga,
        private val fetchChaptersFromSource: FetchChaptersFromSource,
        private val sourceManager: SourceManager,
        private val schedulers: RxSchedulers
) : RxViewModel() {

    init {
        Timber.d("FUCK enter")
    }
    val mangaLiveData: MutableLiveData<Resource<Manga>> = MutableLiveData()

    val chaptersLiveData: MutableLiveData<Resource<List<Chapter>>> = MutableLiveData()

    init {
        mangaLiveData.postValue(Resource.Loading())
    }

    var mangaId: Long = 0
        set(value) {
            field = value
            getManga.interact(mangaId)
                    .subscribeOn(schedulers.io)
                    .observeOn(schedulers.io)
                    .concatMap { manga ->
                        updateManga.interact(manga)
                    }
                    .subscribe {
                        manga = it
                        mangaLiveData.postValue(Resource.Success(it))
                    }.addTo(disposables)
        }

    fun fetchChapters(manga: Manga) {
        val source =  sourceManager.getOrStub(manga.source)
        fetchChaptersFromSource.interact(source, manga, 1)
                .subscribeOn(schedulers.io)
                .observeOn(schedulers.main)
                .subscribe({
                    chapters = it.list
                    chaptersLiveData.postValue(Resource.Success(chapters))
                }, {
                    chaptersLiveData.postValue(Resource.Error(it.message))
                }).addTo(disposables)
    }

    lateinit var chapters: List<Chapter>

    lateinit var manga: Manga

}
