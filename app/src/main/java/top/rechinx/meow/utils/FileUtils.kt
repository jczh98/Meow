package top.rechinx.meow.utils

import android.content.Context
import io.reactivex.Observable
import top.rechinx.meow.support.relog.ReLog
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.IOException
import java.nio.charset.Charset

object FileUtils {

    fun loadTextFromAsset(context: Context, name: String): String {
        var iss = context.assets.open(name);
        var lenght = iss.available()
        var  buffer = ByteArray(lenght)
        iss.read(buffer)
        return String(buffer, Charset.forName("utf8"))
    }

    fun loadFiles(path: String): List<File>? {
        val file = File(path)
        var strs: Array<File>? = arrayOf()
        if (file.exists()) {
            strs = file.listFiles()
        }
        return strs?.toList()
    }
    
    fun readTextFromSDcard(path: String): String? {
        return readTextFromSDcard(File(path))
    }

    fun readTextFromSDcard(file: File): String? {
        if (!file.exists()) {
            return null
        }

        try {
            val fileInputStream = FileInputStream(file)
            val availableLength = fileInputStream.available()
            val buffer = ByteArray(availableLength)
            fileInputStream.read(buffer)
            fileInputStream.close()

            return String(buffer, Charset.forName("UTF-8"))

        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        return null
    }
}