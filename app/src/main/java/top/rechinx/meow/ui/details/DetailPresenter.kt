package top.rechinx.meow.ui.details

import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.repository.MangaRepository
import top.rechinx.meow.support.log.L
import top.rechinx.meow.support.mvp.RxPresenter

class DetailPresenter(private val mangaRepository: MangaRepository): RxPresenter<DetailContract.View>(), DetailContract.Presenter {

    override fun markedAsHistory(manga: Manga) {
        manga.history = true
        rx {
            mangaRepository.updateManga(manga)
        }
    }
    override fun favoriteOrNot(manga: Manga) {
        manga.favorite = !manga.favorite
        rx {
            mangaRepository.updateManga(manga)
        }
    }

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