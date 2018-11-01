package top.rechinx.meow.core.source.model

import io.reactivex.processors.FlowableProcessor
import io.reactivex.processors.PublishProcessor
import io.reactivex.subjects.Subject

open class AbsMangaPage(
        val index: Int,
        val url: String,
        var imageUrl: String?
) {

    val number: Int
        get() = index + 1

    @Transient @Volatile var status: Int = 0
        set(value) {
            field = value
            statusProcessor?.onNext(value)
        }

    @Transient private var statusProcessor: FlowableProcessor<Int>? = null

    fun setStatusProcessor(processor: FlowableProcessor<Int>?) {
        this.statusProcessor = processor
    }

    companion object {

        const val QUEUE = 0
        const val LOAD_PAGE = 1
        const val DOWNLOAD_IMAGE = 2
        const val READY = 3
        const val ERROR = 4
    }
}