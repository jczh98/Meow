package top.rechinx.meow.ui.manga

import top.rechinx.meow.core.source.SourceManager
import top.rechinx.meow.domain.chapter.interactor.FetchChaptersFromSource
import top.rechinx.meow.domain.manga.interactor.GetManga
import top.rechinx.meow.domain.manga.interactor.UpdateManga
import top.rechinx.meow.rikka.rx.RxSchedulers
import javax.inject.Inject

class MangaInfoViewModelFactory @Inject constructor(
        private val getManga: GetManga,
        private val updateManga: UpdateManga,
        private val fetchChaptersFromSource: FetchChaptersFromSource,
        private val sourceManager: SourceManager,
        private val schedulers: RxSchedulers
) {
    fun create(mangaId: Long): MangaInfoViewModel {
        return MangaInfoViewModel(mangaId, getManga, updateManga, fetchChaptersFromSource, sourceManager, schedulers)
    }
}
