package top.rechinx.meow.engine

import android.app.Application
import com.bumptech.glide.load.model.GlideUrl
import com.bumptech.glide.load.model.LazyHeaders
import io.reactivex.*
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.functions.Function
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import org.reactivestreams.Publisher
import org.w3c.dom.Element
import org.w3c.dom.Node
import top.rechinx.meow.App
import top.rechinx.meow.core.JsonIterator
import top.rechinx.meow.model.Chapter
import top.rechinx.meow.model.Comic
import top.rechinx.meow.model.ImageUrl
import top.rechinx.meow.network.Api
import top.rechinx.meow.support.relog.ReLog
import java.nio.charset.Charset
import java.util.*
import java.util.Observer

class SaSource {

    lateinit var title: String
    lateinit var name: String

    private var mAttrs: MutableMap<String, String> = HashMap()
    private lateinit var root: Element
    private lateinit var js: JsEngine
    private var version: Int = 0


    private lateinit var searchNode: SaNode
    private lateinit var comicNode: SaNode
    private lateinit var chapterNode: SaNode
    private var coverNode: SaNode? = null
    private var imageNode: SaNode? = null

    constructor(app: Application, xml: String) {
        initSource(app, xml)
    }

    private fun initSource(app: Application, xml: String) {
        root = Helper.getXmlRoot(xml)
        for(i in 0 until root.attributes.length) {
            mAttrs[root.attributes.item(i).nodeName] = root.attributes.item(i).nodeValue
        }
        version = mAttrs["ver"]?.toInt()!!

        var head = root.getElementsByTagName("head").item(0)
        var body = root.getElementsByTagName("body").item(0)
        var jscript = root.getElementsByTagName("jscript").item(0)

        for(i in 0 until head.childNodes.length) {
            val node = head.childNodes.item(i)
            if(node.nodeType == Node.ELEMENT_NODE) {
                if(node.firstChild.nodeType == Node.TEXT_NODE) {
                    mAttrs[node.nodeName] = node.firstChild.nodeValue
                }
            }
        }

        var bodyList = HashMap<String, Node>()
        for(i in 0 until body.childNodes.length) {
            val node = body.childNodes.item(i)
            if(node.nodeType == Node.ELEMENT_NODE) {
                bodyList[node.nodeName] = node
            }
        }

        name = mAttrs["name"]!!
        title = mAttrs["title"]!!

        searchNode = SaNode(bodyList["search"]!! as Element)
        comicNode = SaNode(bodyList["comic"]!! as Element)
        chapterNode = SaNode(bodyList["chapter"]!! as Element)
        coverNode = SaNode(bodyList["cover"] as Element)
        imageNode = SaNode(bodyList["image"] as Element)

        var code = (jscript as Element).getElementsByTagName("code").item(0).textContent
        js = JsEngine(app)
        js.loadJs(code)
    }

    fun getObservable(node: SaNode, keyword: String?, secondKeyword: String?, page: Int?): Observable<String> {
        return Observable.create(ObservableOnSubscribe<String> {
            try {
                var request = Helper.getRequest(node, keyword, secondKeyword, page)
                var html = getResponseBody(App.getHttpClient()!!, request!!)
                val json = js.rxCallJs(node.parser!!, html).blockingFirst()
                it.onNext(json)
                it.onComplete()
            } catch (e: Exception) {
                it.onError(e)
            }
        }).subscribeOn(Schedulers.io())
    }

    fun getSearchResult(keyword: String, page: Int): Observable<Comic> {
        return getObservable(searchNode, keyword, null, page)
                .flatMap(Function<String, Observable<Comic>> {
                    return@Function Observable.create(ObservableOnSubscribe<Comic> { emitter ->
                        try {
                            val iterator = SaIterator(JSONArray(it))
                            if(iterator == null || iterator.empty()) {
                                throw Exception()
                            }
                            while(iterator.hasNext()) {
                                var comic = iterator.next()
                                if(comic != null) {
                                    comic.headers = coverNode?.headers
                                    comic.source = name
                                    comic.sourceName = title
                                    emitter.onNext(comic)
                                    Thread.sleep(Random().nextInt(200).toLong())
                                }
                            }
                            emitter.onComplete()
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    })
                })
    }

    fun getComicInfo(comic: Comic, page: Int): Observable<List<Chapter>> {
        return getObservable(comicNode, comic.cid, null, page)
                .flatMap(Function<String, Observable<List<Chapter>>> {
                    return@Function Observable.create(ObservableOnSubscribe<List<Chapter>> { emitter ->
                        try {
                            val jsonArr = JSONArray(it)
                            val jsonObj = jsonArr.getJSONObject(0)
                            val title = jsonObj.getString("title")
                            val cover = jsonObj.getString("cover")
                            val intro = jsonObj.getString("intro")
                            val author = jsonObj.getString("author")
                            val update = jsonObj.getString("update")
                            val isPage = jsonObj.getBoolean("isPage")
                            comic.setInfo(title, cover, update, intro, author, false, isPage, coverNode?.headers, title)
                            val jsonObj2 = jsonArr.getJSONObject(1)
                            val chapters = jsonObj2.getJSONArray("chapters")
                            val list = LinkedList<Chapter>()
                            for(i in 0 until chapters.length()) {
                                val item = chapters.getJSONObject(i)
                                val title = item.getString("title")
                                val chapterId = item.getString("chapterId")
                                list.add(Chapter(title, chapterId))
                            }
                            emitter.onNext(list)
                            emitter.onComplete()
                        } catch (e: Exception) {
                            emitter.onError(e)
                        }
                    })
                })
    }

    fun getChapterImage(cid: String, chapterId: String): Observable<List<ImageUrl>> {
        return getObservable(chapterNode, cid, chapterId, null)
                .flatMap(Function<String, Observable<List<ImageUrl>>> {
                    return@Function Observable.create(ObservableOnSubscribe<List<ImageUrl>> { emitter ->
                        try {
                            val array = JSONArray(it)
                            val list = LinkedList<ImageUrl>()
                            for(i in 0 until array.length()) {
                                list.add(ImageUrl(i + 1, array.getString(i), chapterId, imageNode?.headers))
                            }
                            emitter.onNext(list)
                            emitter.onComplete()
                        }catch (e: Exception) {
                            emitter.onError(e)
                        }
                    })
                })
    }

    private fun getResponseBody(client: OkHttpClient, request: Request): String {
        var response: Response? = null
        try {
            response = client.newCall(request).execute()
            if (response!!.isSuccessful) {
                val bodybytes = response.body()!!.bytes()
                var body = String(bodybytes)
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
        throw Api.NetworkErrorException()
    }
    fun getVersion() = version
}