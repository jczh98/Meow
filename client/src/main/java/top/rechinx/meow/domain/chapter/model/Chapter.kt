package top.rechinx.meow.domain.chapter.model

data class Chapter(
        val id: Long = 0,
        val mangaId: Long,
        val key: String,
        val name: String,
        var progress: Int = 0, // last read page
        val date_upload: Long = 0,
        val number: Int = 0,
        val download: Boolean = false,
        val complete: Boolean = false
)