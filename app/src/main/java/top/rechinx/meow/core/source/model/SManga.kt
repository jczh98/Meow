package top.rechinx.meow.core.source.model

abstract class SManga {

    abstract var cid: String?

    abstract var title: String?

    abstract var author: String?

    abstract var description: String?

    abstract var genre: String?

    abstract var status: Int

    abstract var thumbnail_url: String?

    abstract var initialized: Boolean

    fun copyFrom(other: SManga) {
        if (other.cid != null)
            cid = other.cid

        if (other.author != null)
            author = other.author

        if (other.author != null)
            author = other.author

        if (other.description != null)
            description = other.description

        if (other.genre != null)
            genre = other.genre

        if (other.thumbnail_url != null)
            thumbnail_url = other.thumbnail_url

        if (other.title != null)
            title = other.title

        status = other.status

        if (!initialized)
            initialized = other.initialized
    }

    companion object {
        const val UNKNOWN = 0
        const val ONGOING = 1
        const val COMPLETED = 2

        fun create(): SManga {
            return SMangaImpl()
        }
    }
}