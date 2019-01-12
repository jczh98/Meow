package top.rechinx.meow.domain.chapter.model

data class ChaptersPage(
        val number: Int,
        val chapters: List<Chapter>,
        val hasNextPage: Boolean
)