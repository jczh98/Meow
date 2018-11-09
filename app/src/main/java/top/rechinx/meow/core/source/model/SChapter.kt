package top.rechinx.meow.core.source.model

interface SChapter {

    var url: String?

    var name: String?

    var date_updated: Long

    var chapter_number: String?

    fun copyFrom(other: SChapter) {
        if(other.url != null) {
            this.url = other.url
        }

        if(other.name != null) {
            this.name = other.name
        }

        this.date_updated = other.date_updated
    }

    companion object {
        fun create(): SChapter {
            return SChapterImpl()
        }
    }
}