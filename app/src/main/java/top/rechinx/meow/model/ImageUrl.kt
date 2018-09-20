package top.rechinx.meow.model


data class ImageUrl(var id: Int,
                    var page_number: Int,
                    var imageUrl: String?,
                    var chapter: String?,
                    var headers: String?) {

    constructor(number: Int, imageUrl: String?, chapter: String?, headers: String?): this(0, number, imageUrl, chapter, headers)

}