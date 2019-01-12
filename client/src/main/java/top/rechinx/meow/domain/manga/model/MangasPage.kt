package top.rechinx.meow.domain.manga.model

data class MangasPage(
        val number: Int,
        val mangas: List<Manga>,
        val hasNextPage: Boolean
)