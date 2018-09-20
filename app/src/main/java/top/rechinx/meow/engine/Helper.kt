package top.rechinx.meow.engine

import com.bumptech.glide.load.model.LazyHeaders
import okhttp3.Headers
import okhttp3.Request
import org.w3c.dom.Element
import org.xml.sax.InputSource
import top.rechinx.meow.support.relog.ReLog
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object Helper {

    fun getXmlRoot(xml: String): Element {
        val sr = StringReader(xml)
        val factory = DocumentBuilderFactory.newInstance()
        val domBuilder = factory.newDocumentBuilder()
        return domBuilder.parse(InputSource(sr)).documentElement
    }

    fun getRequest(node: SaNode, keyword: String?, secondKeyword: String?, page: Int?): Request? {
        var url = node.url
        if(keyword != null) url = url?.replace("@key", keyword)
        if(secondKeyword != null) url = url?.replace("@skey", secondKeyword)
        if(page != null) {
            if(url?.indexOf("@page") == -1) {
                if(page > 0) return null
            }
            url = url?.replace("@page", page.toString())
        }
        var builder = Request.Builder().url(url)
        if(node.ua != null) builder.addHeader("User-Agent", node.ua)
        return builder.build()
    }

    fun parseHeaders(headers: String?): LazyHeaders {
        var lazyHeaders =  LazyHeaders.Builder()
        if(headers == null) return lazyHeaders.build()
        for(kv in headers.split(";")) {
            val idx = kv.indexOf("=")
            val k = kv.substring(0, idx).trim()
            val v = kv.substring(idx+1).trim()
            lazyHeaders.addHeader(k, v)
        }
        return lazyHeaders.build()
    }

    fun getElement(n: Element, tag: String): Element? {
        val temp = n.getElementsByTagName(tag)
        return if (temp.length > 0)
            temp.item(0) as Element
        else
            null
    }

    fun getSearchRequest(node: SaNode, keyword: String, page: Int): Request? {
        var url = node.url
        url = url?.replace("@key", keyword)
        url = url?.replace("@page", page.toString())
        return Request.Builder().url(url).build()
    }

    fun getInfoRequest(node: SaNode, cid: String, page: Int): Request? {
        var url = node.url
        url = url?.replace("@key", cid)
        url = url?.replace("@key", page.toString())
        return Request.Builder().url(url).build()
    }

    fun getImageRequest(node: SaNode, cid: String, chapterId: String): Request? {
        var url = node.url
        url = url?.replace("@key", cid)
        url = url?.replace("@skey", chapterId)
        return Request.Builder().url(url).build()
    }
}