package top.rechinx.meow.data.cache

import android.app.Application
import android.content.Context
import top.rechinx.meow.utils.DiskUtil
import java.io.File
import javax.inject.Inject

class CoverCache @Inject constructor(
        val context: Application
) {

    private val cacheDir = context.getExternalFilesDir("covers") ?:
            File(context.filesDir, "covers").also { it.mkdirs() }

    fun getCoverFile(thumbnailUrl: String): File {
        return File(cacheDir, DiskUtil.hashKeyForDisk(thumbnailUrl))
    }
}