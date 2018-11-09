package top.rechinx.meow.core.source.model

class SMangaImpl: SManga() {

    override var cid: String? = null

    override var title: String? = null

    override var author: String? = null

    override var description: String? = null

    override var genre: String? = null

    override var status: Int = 0

    override var thumbnail_url: String? = null

    override var initialized: Boolean = false
}