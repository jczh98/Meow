package top.rechinx.meow.utils

import android.content.Context
import io.reactivex.Observable
import top.rechinx.meow.App
import top.rechinx.meow.support.log.L
import java.io.*
import java.nio.charset.Charset

object FileUtils {

    fun loadTextFromAsset(context: Context, name: String): String {
        var iss = context.assets.open(name);
        var lenght = iss.available()
        var  buffer = ByteArray(lenght)
        iss.read(buffer)
        return String(buffer, Charset.forName("utf8"))
    }

    fun loadFiles(path: String): Observable<List<File>> {
        val file = File(path)
        var strs: Array<File>? = arrayOf()
        if (file.exists()) {
            strs = file.listFiles()
        }
        return Observable.fromArray(strs?.toList())
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

    fun asset2Sdcard(app: App) {
        val manager = app.assets
        try {
            var files = manager.list("sites")
            for(file in files) {
                L.d(file)
                var ins = manager.open("sites/$file")
                var outFile = File(app.getBasePath(), file)
                var ous = FileOutputStream(outFile)
                copyFile(ins, ous)
                ins.close()
                ous.flush()
                ous.close()
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun copyFile(ins: InputStream, out: OutputStream) {
        val buffer = ByteArray(1024)
        var read: Int = ins.read(buffer)
        while(read != -1) {
            out.write(buffer, 0, read)
            read = ins.read(buffer)
        }
    }
}