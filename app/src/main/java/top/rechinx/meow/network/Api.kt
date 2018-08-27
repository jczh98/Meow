package top.rechinx.meow.network

import io.reactivex.Observable
import io.reactivex.ObservableOnSubscribe
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import top.rechinx.meow.App
import top.rechinx.meow.core.Parser
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.support.relog.ReLog
import java.nio.charset.Charset
import java.util.*

object  Api {

    fun getSearchResult(parser: Parser, keyword: String, page: Int): Observable<Comic> {
        return Observable.create(ObservableOnSubscribe<Comic> {
            try {
                var request = parser.getSearchRequest(keyword, page)
                var html = getResponseBody(App.getHttpClient()!!, request!!)
                var iterator = parser.getSearchIterator(html, page)
                if(iterator == null || iterator.empty()) {
                    throw Exception()
                }
                while(iterator.hasNext()) {
                    var comic = iterator.next()
                    if(comic != null) {
                        ReLog.d("cid is ${comic.cid}, title is ${comic.title}, cover is ${comic.image}")
                        it.onNext(comic)
                        Thread.sleep(Random().nextInt(200).toLong())
                    }
                }
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }).subscribeOn(Schedulers.io())
    }

    @Throws(NetworkErrorException::class)
    private fun getResponseBody(client: OkHttpClient, request: Request): String {
        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            if (response!!.isSuccessful) {
                val bodybytes = response.body()!!.bytes()
                var body = String(bodybytes)
                ReLog.d(body)
                if (body.indexOf("charset=gb2312") != -1) {
                    body = String(bodybytes, Charset.forName("GB2312"))
                }
                return body
            }
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            if (response != null) {
                response.close()
            }
        }
        throw NetworkErrorException()
    }

    fun getChapterImage(parser: Parser, cid: String, chapterId: String): Observable<List<ImageUrl>> {
        return Observable.create(ObservableOnSubscribe<List<ImageUrl>> {
            try {
                var request = parser.getImageRequest(cid, chapterId)
                var html = getResponseBody(App.getHttpClient()!!, request!!)
                var list = parser.parseImage(html)
                if(list != null) {
                    if(list.isEmpty()) {
                        throw Exception()
                    } else {
                        for(imageUrl in list) {
                            imageUrl.chapter = chapterId
                        }
                        it.onNext(list)
                        it.onComplete()
                    }
                }
            } catch (e: Exception) {
                it.onError(e)
            }
        }).subscribeOn(Schedulers.io())
    }

    fun getComicInfo(parser: Parser, comic: Comic): Observable<List<Chapter>> {
        return Observable.create(ObservableOnSubscribe<List<Chapter>> {
            try {
                var request = parser.getInfoRequest(comic.cid!!)
                var html = getResponseBody(App.getHttpClient()!!, request!!)
                parser.parserInto(html, comic)
                val list = parser.parseChapter(html)
                if (list != null) {
                    ReLog.d(list.isEmpty().toString())
                    if(!list.isEmpty()) {
                        it.onNext(list)
                        it.onComplete()
                    } else {
                        throw ParseErrorException()
                    }
                }
            } catch (e: Exception) {
                it.onError(e)
            }
        }).subscribeOn(Schedulers.io())
    }


    class ParseErrorException : Exception()
    class NetworkErrorException : Exception()
}