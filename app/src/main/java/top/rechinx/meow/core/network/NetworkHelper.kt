package top.rechinx.meow.core.network

import android.content.Context
import com.facebook.stetho.okhttp3.StethoInterceptor
import okhttp3.OkHttpClient

class NetworkHelper(context: Context) {

    val client = OkHttpClient.Builder().addNetworkInterceptor(StethoInterceptor()).build()
}