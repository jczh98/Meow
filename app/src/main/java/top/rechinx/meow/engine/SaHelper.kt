package top.rechinx.meow.engine

import org.w3c.dom.Element
import org.xml.sax.InputSource
import java.io.StringReader
import javax.xml.parsers.DocumentBuilderFactory

object SaHelper {

    fun getXmlRoot(xml: String): Element {
        val sr = StringReader(xml)
        val factory = DocumentBuilderFactory.newInstance()
        val domBuilder = factory.newDocumentBuilder()
        return domBuilder.parse(InputSource(sr)).documentElement
    }

    fun getElement(n: Element, tag: String): Element? {
        val temp = n.getElementsByTagName(tag)
        return if (temp.length > 0)
            temp.item(0) as Element
        else
            null
    }
}