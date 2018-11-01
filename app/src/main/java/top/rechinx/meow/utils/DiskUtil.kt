package top.rechinx.meow.utils

import java.io.File

object DiskUtil {

    fun hashKeyForDisk(key: String): String {
        return Hash.md5(key)
    }
    
    fun getDirectorySize(f: File): Long {
        var size: Long = 0
        if (f.isDirectory) {
            for (file in f.listFiles()) {
                size += getDirectorySize(file)
            }
        } else {
            size = f.length()
        }
        return size
    }

}