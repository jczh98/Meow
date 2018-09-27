package top.rechinx.meow

import org.json.JSONArray
import org.junit.Test

import org.w3c.dom.Element
import org.w3c.dom.Node
import top.rechinx.meow.engine.Helper
import java.io.*
import java.nio.charset.Charset
import java.util.HashMap

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * See [testing documentation](http://d.android.com/tools/testing).
 */
class ExampleUnitTest {

    fun readToString(file: File): String? {
        val encoding = "UTF-8"
        val filelength = file.length()
        val filecontent = ByteArray(filelength.toInt())
        try {
            val `in` = FileInputStream(file)
            `in`.read(filecontent)
            `in`.close()
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        }

        try {
            return String(filecontent, Charset.forName(encoding))
        } catch (e: UnsupportedEncodingException) {
            System.err.println("The OS does not support $encoding")
            e.printStackTrace()
            return null
        }
    }

    fun print(str: String) = System.out.println(str)

    @Test
    fun testEngine() {
//        val file = File("/Users/chin/workshop/Meow/app/src/test/java/top/rechinx/meow/test.xml")
//        //System.out.println(readToString(file))
//        var root = Helper.getXmlRoot(readToString(file)!!)
//        var head = root.getElementsByTagName("head").item(0)
//        var body = root.getElementsByTagName("body").item(0) as Element
//        var bodyList = HashMap<String, Node>()
//        for(i in 0 until head.childNodes.length) {
//            val node = head.childNodes.item(i)
//            if(node.nodeType == Node.ELEMENT_NODE) {
//                if(node.firstChild.nodeType == Node.TEXT_NODE) {
//                    print(node.nodeName + " " + node.firstChild.nodeValue)
//                }
//            }
//        }


    }
}
