package top.rechinx.meow.core.network

import io.reactivex.Observable
import io.reactivex.ObservableEmitter
import io.reactivex.ObservableOnSubscribe
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.reactivestreams.Subscription
import java.util.concurrent.atomic.AtomicBoolean

fun Call.asObservable(): Observable<Response> {
    return Observable.create { emitter ->
        try {
            val call = clone()
            val response = call.execute()
            if(!emitter.isDisposed) {
                emitter.onNext(response)
                emitter.onComplete()
            }
        }catch (e: Exception) {
            if(!emitter.isDisposed) {
                emitter.onError(e)
            }
        }
    }
}


fun Call.asObservableSuccess(): Observable<Response> {
    return asObservable().doOnNext { response ->
        if (!response.isSuccessful) {
            response.close()
            throw Exception("HTTP error ${response.code()}")
        }
    }
}

fun OkHttpClient.newCallWithProgress(request: Request, listener: ProgressListener): Call {
    val progressClient = newBuilder()
            .cache(null)
            .addNetworkInterceptor { chain ->
                val originalResponse = chain.proceed(chain.request())
                originalResponse.newBuilder()
                        .body(ProgressResponseBody(originalResponse.body()!!, listener))
                        .build()
            }
            .build()

    return progressClient.newCall(request)
}
