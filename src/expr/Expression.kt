package expr

import java.util.ArrayList

class Expression {
    var body: String = ""
    var complexQueries: ArrayList<String>? = null
    var errors = false
}

var ops =
    mapOf("NOT" to 10, "AND" to 5,"DAND" to 5, "OR" to 5, "RANGE#" to 10, "SENTENCE" to 5, "PARAGRAPH" to 5)
var binary = arrayOf("AND", "OR", "SENTENCE", "PARAGRAPH")

var unary = arrayOf("NOT")

var rawWords = arrayOf("SENTENCE", "PARAGRAPH")

fun isOp(token: String): Boolean {
    if (ops.containsKey(token) || token.startsWith("RANGE#")) return true
    return false
}

fun isComplex(token: String): Boolean {
    if (token.startsWith('"') && token.endsWith('"')) return true
    return false
}