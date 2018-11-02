package top.rechinx.meow.core.network

interface ProgressListener {
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)
}