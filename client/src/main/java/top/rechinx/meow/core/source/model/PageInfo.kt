package top.rechinx.meow.core.source.model

import io.reactivex.processors.FlowableProcessor
import top.rechinx.meow.core.network.ProgressListener

open class PageInfo(
        val index: Int,
        val url: String,
        var imageUrl: String?
) : ProgressListener {

    val number: Int
        get() = index + 1

    @Transient @Volatile var status: Int = 0
        set(value) {
            field = value
            statusProcessor?.onNext(value)
        }
    @Transient @Volatile var progress: Int = 0


    @Transient private var statusProcessor: FlowableProcessor<Int>? = null

    override fun update(bytesRead: Long, contentLength: Long, done: Boolean) {
        progress = if (contentLength > 0) {
            (100 * bytesRead / contentLength).toInt()
        } else {
            -1
        }
    }

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