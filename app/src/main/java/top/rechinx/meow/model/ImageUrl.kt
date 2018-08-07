package top.rechinx.meow.model

data class ImageUrl(var id: Int,
                    var page_number: Int,
                    var urls: Array<String>,
                    var chapter: String?) {

    constructor(number: Int, urls: Array<String>): this(0, number, urls, null)

    constructor(number: Int, url: String): this(number, arrayOf(url))
}