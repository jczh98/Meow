package top.rechinx.meow.soup

import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.util.*

class Node {

    private lateinit var element: Element

    constructor(html: String) {
        this.element = Jsoup.parse(html).body()
    }

    constructor(element: Element) {
        this.element = element
    }

    fun list(cssQuery: String): List<Node> {
        var list = LinkedList<Node>()
        var elements = element.select(cssQuery)
        for (e in elements) {
            list.add(Node(e))
        }
        return list
    }

    fun attr(attr: String): String? {
        try {
            return element.attr(attr).trim()
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun src(): String? = attr("src")
}