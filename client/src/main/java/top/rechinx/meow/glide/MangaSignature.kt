package top.rechinx.meow.glide

import com.bumptech.glide.load.Key
import top.rechinx.meow.domain.manga.model.Manga
import java.io.File
import java.security.MessageDigest

class MangaSignature(manga: Manga, file: File) : Key {

    private val key = manga.cover + file.lastModified()

    override fun equals(other: Any?): Boolean {
        return if (other is MangaSignature) {
            key == other.key
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return key.hashCode()
    }

    override fun updateDiskCacheKey(md: MessageDigest) {
        md.update(key.toByteArray(Key.CHARSET))
    }
}