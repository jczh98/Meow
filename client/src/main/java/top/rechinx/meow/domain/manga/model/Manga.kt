package top.rechinx.meow.domain.manga.model

import top.rechinx.meow.core.source.model.MangaInfo

data class Manga(
        val id: Long = -1,
        val source: Long,
        val key: String,
        val title: String,
        val artist: String = "",
        val author: String = "",
        val description: String = "",
        val genres: String = "",
        val status: Int = MangaInfo.UNKNOWN,
        val cover: String = "",
        val favorite: Boolean = false,
        val lastUpdate: Long = 0,
        val initialized: Boolean = false,
        val viewer: Int = 0,
        val last_chapter_key: String = "",
        val last_chapter_id: Long = 0,
        val viewed: Boolean = false,
        val downloaded: Boolean = false
)