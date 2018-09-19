package top.rechinx.meow.engine

import org.w3c.dom.Element

class SaNode: INode {

    var name: String? = null
    var url: String? = null
    var parser: String? = null
    var headers: String? = null
    var ua: String? = null

    private var attrList = SaAttrList()

    constructor(element: Element?) {
        if(element != null) {
            travelAttrs(element)
            name = attrList.getString("name")
            parser = attrList.getString("parser")
            headers = attrList.getString("headers")
            url = attrList.getString("url")
            ua = attrList.getString("ua")
        }
    }

    fun travelAttrs(element: Element) {
        val attrs = element.attributes
        for(i in 0 until attrs.length) {
            attrList[attrs.item(i).nodeName] = attrs.item(i).nodeValue
        }
    }

    override fun getNodeName(): String = name!!

}