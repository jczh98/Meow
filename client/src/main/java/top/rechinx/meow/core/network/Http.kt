package top.rechinx.meow.core.network

import okhttp3.Cache
import okhttp3.OkHttpClient

class Http(cache: Cache) {

    val defaultClient = OkHttpClient.Builder()
            .cache(cache)
            .build()
}