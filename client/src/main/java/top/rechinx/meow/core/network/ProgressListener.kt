package top.rechinx.meow.core.network

/**
 * A listener who receives updates of the progress of network responses.
 */
interface ProgressListener {

    /**
     * Called every time a new chunk of data is read. [bytesRead] contains the currently read bytes,
     * [contentLength] shows the total size of the content if the server sent the corresponding
     * header, otherwise it will be -1, and [done] especify whether the source is already exhausted.
     *
     * This function should return quickly due to the amount of times it's called.
     */
    fun update(bytesRead: Long, contentLength: Long, done: Boolean)

}