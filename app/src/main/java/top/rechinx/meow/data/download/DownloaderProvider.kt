package top.rechinx.meow.data.download

import android.content.ContentResolver
import android.net.Uri
import com.hippo.unifile.UniFile
import org.json.JSONArray
import org.json.JSONException
import org.json.JSONObject
import top.rechinx.meow.App
import top.rechinx.meow.core.source.Source
import top.rechinx.meow.core.source.model.MangaPage
import top.rechinx.meow.data.database.model.Chapter
import top.rechinx.meow.data.database.model.Manga
import top.rechinx.meow.data.database.model.Task
import top.rechinx.meow.data.preference.getOrDefault
import top.rechinx.meow.utils.DiskUtil
import top.rechinx.meow.utils.UniFileUtils
import io.reactivex.Observable
import top.rechinx.meow.ui.reader.model.ReaderPage

object DownloaderProvider {

    private val JSON_KEY_VERSION = "version"
    private val JSON_KEY_TYPE = "type"
    private val JSON_KEY_TYPE_MANGA = "manga"
    private val JSON_KEY_TYPE_CHAPTER = "chapter"
    private val JSON_KEY_MANGA_LIST = "list"
    private val JSON_KEY_MANGA_SOURCE = "source"
    private val JSON_KEY_MANGA_URL = "url"
    private val JSON_KEY_MANGA_TITLE = "title"
    private val JSON_KEY_MANGA_COVER = "cover"
    private val JSON_KEY_CHAPTER_PATH = "path"
    private val JSON_KEY_CHAPTER_TITLE = "title"

    private val DOWNLOAD = "download"
    private val FILE_INDEX = "index.cdif"
    private val NO_MEDIA = ".nomedia"

    fun updateMangaIndex(resolver: ContentResolver, root: UniFile, list: List<Chapter>, manga: Manga, source: Source) {
        try {
            createNoMedia(root)
            val jsonString = getJsonFromManga(list, manga)
            val file = createMangaIndex(root, manga, source)
            UniFileUtils.writeStringToFile(resolver, file, "meow$jsonString")
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }

    private fun createMangaIndex(root: UniFile, manga: Manga, source: Source): UniFile? {
        val source = UniFileUtils.getOrCreateSubDirectory(root, source.name)
        val dir = UniFileUtils.getOrCreateSubDirectory(source, manga.title)
        return if (dir != null) {
            UniFileUtils.getOrCreateFile(dir, FILE_INDEX)
        } else null
    }

    @Throws(JSONException::class)
    private fun getJsonFromManga(list: List<Chapter>, manga: Manga): String {
        val obj = JSONObject()
        obj.put(JSON_KEY_VERSION, "1")
        obj.put(JSON_KEY_TYPE, JSON_KEY_TYPE_MANGA)
        obj.put(JSON_KEY_MANGA_SOURCE, manga.sourceId)
        obj.put(JSON_KEY_MANGA_URL, manga.url)
        obj.put(JSON_KEY_MANGA_TITLE, manga.title)
        obj.put(JSON_KEY_MANGA_COVER, manga.thumbnail_url)
        val array = JSONArray()
        for (chapter in list) {
            val temp = JSONObject()
            temp.put(JSON_KEY_CHAPTER_TITLE, chapter.name)
            temp.put(JSON_KEY_CHAPTER_PATH, chapter.url)
            array.put(temp)
        }
        obj.put(JSON_KEY_MANGA_LIST, array)
        return obj.toString()
    }

    @Throws(JSONException::class)
    private fun getJsonFromChapter(title: String, path: String): String {
        val obj = JSONObject()
        obj.put(JSON_KEY_VERSION, "1")
        obj.put(JSON_KEY_TYPE, JSON_KEY_TYPE_CHAPTER)
        obj.put(JSON_KEY_CHAPTER_TITLE, title)
        obj.put(JSON_KEY_CHAPTER_PATH, path)
        return obj.toString()
    }

    private fun createNoMedia(root: UniFile) {
        UniFileUtils.getOrCreateFile(root, NO_MEDIA)
    }

    fun updateChapterIndex(resolver: ContentResolver, root: UniFile, task: Task) : UniFile? {
        try {
            val jsonString = getJsonFromChapter(task.title!!, task.path!!)
            val dir1 = UniFileUtils.getOrCreateSubDirectory(root, task.sourceName)
            val dir2 = UniFileUtils.getOrCreateSubDirectory(dir1, task.mangaName)
            val dir3 = UniFileUtils.getOrCreateSubDirectory(dir2, DiskUtil.buildValidFilename(task.chapter!!.name!!))
            if (dir3 != null) {
                val file = UniFileUtils.getOrCreateFile(dir3, FILE_INDEX)
                UniFileUtils.writeStringToFile(resolver, file, "meow$jsonString")
                return dir3
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return null
    }

    fun buildReaderPages(source: Source, manga: Manga, chapter: Chapter, rootDirectory: String, resolver: ContentResolver) : Observable<List<ReaderPage>> {
        val chapterDir = findChapterDir(getUniFileDirectory(rootDirectory),
                chapter, manga, source)
        return Observable.fromCallable {
            val files = chapterDir?.listFiles().orEmpty()
                    .filter { "image" in it.type.orEmpty() }
            if (files.isEmpty()) {
                throw Exception("Page list is empty")
            }
            files.sortedBy { it.name }
                    .mapIndexed { i, file ->
                        ReaderPage(i, "", null, {resolver.openInputStream(file.uri)} )
                                .apply { status = MangaPage.READY }
                        }
        }
    }

    fun getUniFileDirectory(rootDirectory: String) : UniFile {
        return UniFile.fromUri(App.instance, Uri.parse(rootDirectory))
    }

    fun findSourceDir(root: UniFile, source: Source): UniFile? {
        return root.findFile(source.name)
    }

    fun findMangaDir(root: UniFile, manga: Manga, source: Source): UniFile? {
        val sourceDir = findSourceDir(root, source)
        return sourceDir?.findFile(getMangaDirName(manga))
    }

    fun findChapterDir(root: UniFile, chapter: Chapter, manga: Manga, source: Source): UniFile? {
        val mangaDir = findMangaDir(root, manga, source)
        return mangaDir?.findFile(getChapterDirName(chapter))
    }

    fun getMangaDirName(manga: Manga): String {
        return DiskUtil.buildValidFilename(manga.title!!)
    }

    fun getChapterDirName(chapter: Chapter): String {
        return DiskUtil.buildValidFilename(chapter.name!!)
    }

}