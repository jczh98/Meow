package top.rechinx.meow.core.source.ext

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception

/**
 * Check response is success, otherwise throw exception
 */
inline fun Response.checkIfSuccess(): Response {
    if (!isSuccessful) {
        close()
        throw Exception("HTTP error ${code()}")
    }
    return this
}

/**
 * Call request with custom client
 */
fun Request.callWithClient(client: OkHttpClient): Response {
    return client.newCall(this).execute().checkIfSuccess()
}