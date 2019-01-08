package top.rechinx.meow.core.network

import io.reactivex.Observable
import io.reactivex.Single
import okhttp3.Call
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

/**
 * Returns a observable of the execution of a network request or an error if the server is unreachable.
 */
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

/**
 * Returns a observable of the execution of a network request or an error if the response is
 * unsuccessful.
 */
fun Call.asObservableSuccess(): Observable<Response> {
    return asObservable().doOnNext { response ->
        if (!response.isSuccessful) {
            response.close()
            throw Exception("HTTP error ${response.code()}")
        }
    }
}

/**
 * Returns a single of the execution of a network request or an error if the server is unreachable.
 * It's the responsibility of the caller to move execution to a background thread.
 */
fun Call.asSingle(): Single<Response> {
    return Single.fromCallable {
        clone().execute()
    }
}

/**
 * Returns a single of the execution of a network request or an error if the response is
 * unsuccessful. It's the responsibility of the caller to move execution to a background thread.
 */
fun Call.asSingleSuccess(): Single<Response> {
    return asSingle().doOnSuccess { response ->
        if (!response.isSuccessful) {
            response.close()
            throw Exception("HTTP error ${response.code()}")
        }
    }
}

/**
 * Returns a new call for this [request] that allows listening for the progress of the response
 * through a [listener].
 */
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