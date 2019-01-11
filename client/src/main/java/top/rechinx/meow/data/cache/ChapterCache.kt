package top.rechinx.meow.data.cache

import android.app.Application
import android.content.Context
import android.text.format.Formatter
import com.google.gson.Gson
import com.jakewharton.disklrucache.DiskLruCache
import okhttp3.Response
import top.rechinx.meow.rikka.ext.saveTo
import top.rechinx.meow.utils.DiskUtil
import java.io.File
import java.io.IOException
import javax.inject.Inject

internal class ChapterCache @Inject constructor(
        val context: Application
) {

    companion object {

        const val PARAMETER_CACHE_DIRECTORY = "chapter_disk_cache"

        const val PARAMETER_APP_VERSION = 1

        const val PARAMETER_VALUE_COUNT = 1

        const val PARAMETER_CACHE_SIZE = 75L * 1024 * 1024
    }

    private val gson = Gson()

    private val diskCache = DiskLruCache.open(File(context.cacheDir, PARAMETER_CACHE_DIRECTORY),
            PARAMETER_APP_VERSION,
            PARAMETER_VALUE_COUNT,
            PARAMETER_CACHE_SIZE)

    val cacheDir: File
        get() = diskCache.directory

    private val realSize: Long
        get() = DiskUtil.getDirectorySize(cacheDir)

    val readableSize: String
        get() = Formatter.formatFileSize(context, realSize)


    fun isImageInCache(imageUrl: String): Boolean {
        try {
            return diskCache.get(DiskUtil.hashKeyForDisk(imageUrl)) != null
        } catch (e: IOException) {
            return false
        }
    }

    fun getImageFile(imageUrl: String): File {
        // Get file from md5 key.
        val imageName = DiskUtil.hashKeyForDisk(imageUrl) + ".0"
        return File(diskCache.directory, imageName)
    }

    @Throws(IOException::class)
    fun putImageToCache(imageUrl: String, response: Response) {
        // Initialize editor (edits the values for an entry).
        var editor: DiskLruCache.Editor? = null

        try {
            // Get editor from md5 key.
            val key = DiskUtil.hashKeyForDisk(imageUrl)
            editor = diskCache.edit(key) ?: throw IOException("Unable to edit key")

            // Get OutputStream and write image with Okio.
            response.body()!!.source().saveTo(editor.newOutputStream(0))

            diskCache.flush()
            editor.commit()
        } finally {
            response.body()?.close()
            editor?.abortUnlessCommitted()
        }
    }
}