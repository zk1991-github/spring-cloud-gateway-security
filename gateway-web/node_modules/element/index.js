var document = require("global/document")
var decode = require("ent").decode
var LRU = require("lru-cache")

var TEXT_NODE = 3

/**
 * Wrap map from jquery.
 */

var map = {
    option: [1, "<select multiple='multiple'>", "</select>"],
    optgroup: [1, "<select multiple='multiple'>", "</select>"],
    legend: [1, "<fieldset>", "</fieldset>"],
    thead: [1, "<table>", "</table>"],
    tbody: [1, "<table>", "</table>"],
    tfoot: [1, "<table>", "</table>"],
    colgroup: [1, "<table>", "</table>"],
    caption: [1, "<table>", "</table>"],
    tr: [2, "<table><tbody>", "</tbody></table>"],
    td: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
    th: [3, "<table><tbody><tr>", "</tr></tbody></table>"],
    col: [2, "<table><tbody></tbody><colgroup>", "</colgroup></table>"],
    _default: [0, ", "]
}
var cache = LRU({
    max: 512,
    maxAge: 1000 * 60 * 60
})

//  parse := (html: String) =>
//      DOMElement | DOMTextNode | DOMDocumentFragment
module.exports = parse

function parse(html){
    if (typeof html !== "string") {
        throw new TypeError("String expected")
    }

    var element = cache.get(html)

    if (!element) {
        // tag name
        element = parseHtml(html)
        cache.set(html, element)
    }

    if (element.nodeType === TEXT_NODE) {
        return document.createTextNode(element.data)
    } else {
        return element.cloneNode(true)
    }
}

function parseHtml(html) {
    var m = /<([\w:]+)/.exec(html)
    if (!m) {
        return document.createTextNode(decode(html))
    }
    var tag = m[1]

    // body support
    var el
    if (tag === "body") {
        el = document.createElement("html")
        el.innerHTML = html
        return el.removeChild(el.lastChild)
    } else {
        // wrap map
        var wrap = map[tag] || map._default
        var depth = wrap[0]
        var prefix = wrap[1]
        var suffix = wrap[2]
        el = document.createElement("div")
        el.innerHTML = prefix + html + suffix

        // trim away wrapper elements
        while (depth--) {
            el = el.lastChild
        }

        var nodes = el.children
        var len = nodes.length

        if (len === 1) {
            return el.removeChild(nodes[0])
        }

        var fragment = document.createDocumentFragment()

        for (var i = 0; i < len; i++) {
            fragment.appendChild(nodes[0])
        }

        return fragment
    }
}
