package top.rechinx.meow.core.source.model

data class MangaInfo(
        val key: String,
        val title: String,
        val artist: String = "",
        val author: String = "",
        val description: String = "",
        val genres: String = "",
        val status: Int = UNKNOWN,
        val cover: String = ""
) {
    companion object {
        const val UNKNOWN = 0
        const val ONGOING = 1
        const val COMPLETED = 2
    }
}