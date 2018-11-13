package top.rechinx.meow.data.cache

import android.content.Context
import top.rechinx.meow.utils.DiskUtil
import java.io.File

class CoverCache(val context: Context) {

    private val cacheDir = context.getExternalFilesDir("covers") ?:
            File(context.filesDir, "covers").also { it.mkdirs() }

    fun getCoverFile(thumbnailUrl: String): File {
        return File(cacheDir, DiskUtil.hashKeyForDisk(thumbnailUrl))
    }
}