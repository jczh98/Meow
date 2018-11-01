package top.rechinx.meow.ui.details

import android.arch.lifecycle.MutableLiveData
import org.koin.standalone.KoinComponent
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.mvvm.RxViewModel
import top.rechinx.meow.support.mvvm.SingleLiveEvent

class DetailViewModel(private val mangaRepository: MangaRepository): RxViewModel(), KoinComponent {


    var manga: MutableLiveData<Manga> = MutableLiveData()
    var fetchInfoError: SingleLiveEvent<Void> = SingleLiveEvent()
    var chapterList: MutableLiveData<List<Chapter>> = MutableLiveData()
    var parserError: SingleLiveEvent<Void> = SingleLiveEvent()

    fun fetchMangaInfo(sourceId: Long, cid: String) {
        rx {
            mangaRepository.fetchMangaInfo(sourceId, cid)
                    .subscribe({
                        this.manga.postValue(it)
                    }, {
                        it.printStackTrace()
                        fetchInfoError.call()
                    })
        }
    }

    fun fetchMangaChapters(sourceId: Long, cid: String) {
        rx {
            mangaRepository.fetchMangaChapters(sourceId, cid)
                    .subscribe({
                        chapterList.postValue(it)
                    }, {
                        it.printStackTrace()
                        parserError.call()
                    })
        }
    }

}