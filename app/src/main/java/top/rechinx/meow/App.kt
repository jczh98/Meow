package top.rechinx.meow

import android.app.Application
import okhttp3.OkHttpClient

class App: Application() {

    companion object {
        private var mHttpClient: OkHttpClient? = null

        fun getHttpClient(): OkHttpClient? {
            if(mHttpClient == null) {
                mHttpClient = OkHttpClient()
            }
            return mHttpClient
        }
    }
}