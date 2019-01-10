package top.rechinx.meow.core.source

import io.reactivex.Observable
import okhttp3.Headers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import top.rechinx.meow.core.network.asObservableSuccess
import top.rechinx.meow.core.source.model.*
import java.security.MessageDigest

abstract class HttpSource(private val dependencies: Dependencies) : CatalogSource {


    /**
     * Id of the source. Generating it by using the first 16 characters (64 bit)
     * of the MD5 of the string "name/versionId"
     */
    override val id: Long by lazy {
        val key = "${name.toLowerCase()}/$versionId"
        val bytes = MessageDigest.getInstance("MD5").digest(key.toByteArray())
        (0..7).map { bytes[it].toLong() and 0xff shl 8 * (7 - it) }.reduce(Long::or) and Long.MAX_VALUE
    }

    /**
     * Version id used to generate the source id. If the site completely changes and urls are
     * incompatible, you may increase this value and it'll be considered as a new source.
     */
    open val versionId = 1

    /**
     * Base url of the website
     */
    abstract val baseUrl: String

    /**
     * Headers use for requests
     */
    val headers: Headers by lazy {
        headersBuilder().build()
    }

    /**
     * Httpclient for network requests
     */
    open val client: OkHttpClient
        get() = dependencies.http.defaultClient

    /**
     * Headers builder for requests. Implementations can override this method for custom headers.
     */
    protected open fun headersBuilder() = Headers.Builder().apply {
        add("User-Agent", "Mozilla/5.0 (Windows NT 6.3; WOW64)")
    }

    /**
     * name of the source.
     */
    override fun toString() = name

}