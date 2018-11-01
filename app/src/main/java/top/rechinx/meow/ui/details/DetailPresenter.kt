package top.rechinx.meow.ui.details

import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.mvp.RxPresenter

class DetailPresenter(private val mangaRepository: MangaRepository): RxPresenter<DetailContract.View>(), DetailContract.Presenter {

    override fun fetchMangaInfo(sourceId: Long, cid: String) {
        rx {
            mangaRepository.fetchMangaInfo(sourceId, cid)
                    .subscribe({
                        view?.onMangaLoadCompleted(it)
                    }, {
                        it.printStackTrace()
                        view?.onMangaFetchError()
                    })
        }
    }

    override fun fetchMangaChapters(sourceId: Long, cid: String) {
        rx {
            mangaRepository.fetchMangaChapters(sourceId, cid)
                    .subscribe({
                        view?.onChaptersInit(it)
                    }, {
                        it.printStackTrace()
                        view?.onChaptersFetchError()
                    })
        }
    }
}