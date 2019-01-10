package top.rechinx.meow.core.source.model

data class ChapterInfo(
        var key: String,
        var name: String,
        var dateUpload: Long,
        var number: Float = -1f
)