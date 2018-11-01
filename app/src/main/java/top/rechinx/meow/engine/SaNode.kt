package top.rechinx.meow.engine

import org.w3c.dom.Element
import org.w3c.dom.Node

class SaNode: INode {

    var name: String? = null
    var url: String? = null
    var parser: String? = null
    var headers: String? = null
    var ua: String? = null
    var lib: String? = null
    var urlbuilder: String? = null
    var headerBuilder: String? = null
    var provider: String? = null
    var auth: String? = null

    private var attrList = SaAttrList()
    private var mItems: ArrayList<SaNode> = ArrayList()

    fun build(element: Element?): SaNode {
        if(element != null) {
            travelAttrs(element)
            name = attrList.getString("name")
            parser = attrList.getString("parser")
            headers = attrList.getString("headers")
            url = attrList.getString("cid")
            ua = attrList.getString("ua")
            urlbuilder = attrList.getString("urlbuilder")
            headerBuilder = attrList.getString("headerbuilder")
            provider = attrList.getString("provide")
            auth = attrList.getString("auth")

            if(element.hasChildNodes()) {
                val list = element.childNodes
                for(i in 0 until list.length) {
                    val node = list.item(i)
                    if(node.nodeType == Node.ELEMENT_NODE) {
                        val ele = node as Element
                        if(ele.tagName == "item") {
                            mItems.add(SaNode().buildItems(ele, this))
                        } else {
                            attrList[ele.tagName] = ele.textContent
                        }
                    }
                }
            }
        }
        return this
    }

    fun buildItems(element: Element, p: SaNode): SaNode {
        travelAttrs(element)
        name = p.name
        lib = attrList.getString("lib")
        return this
    }

    fun travelAttrs(element: Element) {
        val attrs = element.attributes
        for(i in 0 until attrs.length) {
            attrList[attrs.item(i).nodeName] = attrs.item(i).nodeValue
        }
    }

    fun getItems() = mItems

    override fun getNodeName(): String = name!!

}