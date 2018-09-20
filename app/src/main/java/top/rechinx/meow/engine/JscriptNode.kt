package top.rechinx.meow.engine

import android.app.Application
import android.content.res.Resources
import org.w3c.dom.Element
import top.rechinx.meow.App
import top.rechinx.meow.R
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader

class JscriptNode {
    private lateinit var code: String
    private var mRequires: SaNode? = null

    fun build(element: Element): JscriptNode {
        code = Helper.getElement(element, "code")?.textContent!!
        var require = Helper.getElement(element, "require")
        mRequires = SaNode().build(require)
        return this
    }

    fun loadJs(app: Application, js: JsEngine) {
        if(mRequires != null) {
            for(item in mRequires!!.getItems()) {
                if(item.lib != null) {
                    loadLib(app, js, item.lib!!)
                }
            }
        }
        js.loadJs(code)
    }

    private fun loadLib(app: Application, js: JsEngine, lib: String) {
        when(lib) {
            "cheerio" -> {
                loadLib(app.resources, R.raw.cheerio, js)
            }
        }
    }

    private fun loadLib(res: Resources?, resId: Int, js: JsEngine) {
        try {
            val iss = res?.openRawResource(resId)
            val code = iss?.let { readInputStream(it) }
            code?.let { js.loadJs(it) }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    @Throws(IOException::class)
    private fun readInputStream(iss: InputStream): String {
        val input = BufferedReader(InputStreamReader(iss, "utf-8"))
        val buffer = StringBuilder()
        var line = input.readLine()
        while (line != null) {
            buffer.append(line)
            line = input.readLine()
        }
        return buffer.toString()
    }
}