package top.rechinx.meow.rikka.ext

import okio.BufferedSource
import okio.Okio
import java.io.File
import java.io.OutputStream

fun BufferedSource.saveTo(file: File) {
    try {
        // Create parent dirs if needed
        file.parentFile.mkdirs()

        // Copy to destination
        saveTo(file.outputStream())
    } catch (e: Exception) {
        close()
        file.delete()
        throw e
    }
}

fun BufferedSource.saveTo(stream: OutputStream) {
    use { input ->
        Okio.buffer(Okio.sink(stream)).use {
            it.writeAll(input)
            it.flush()
        }
    }
}