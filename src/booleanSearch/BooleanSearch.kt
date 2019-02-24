package booleanSearch

import phraseSearch.IndexWordFinder
import java.util.*

class BooleanSearch(private var dictionary: DocumentWordFinder, private var fileQuantity: Int) {

    private var expression = expr.Expression()

    private var expressionHandler = expr.ExpressionHandler()

    private fun loadQuery(query: String) {
        expression = expressionHandler.getExpression(query)
    }

    //to keep the fileKeeper up to date
    fun loadDictionary(d: DocumentWordFinder, f: Int) {
        dictionary = d
        fileQuantity = f
    }

    fun performQuery(query: String): List<Int> {

        loadQuery(query)

        if (expression.body.isEmpty() || expression.body.isBlank() || expression.errors) return emptyList()

        when (dictionary) {
            is IndexWordFinder -> return emptyList() //TODO:in biword index fix get documents, then just simple query for all
            else -> return simpleQuery()
        }
        return emptyList()
    }


    private fun simpleQuery(): List<Int> {

        val stack = Stack<List<Int>>()

        expression.body.split("\\s+".toRegex()).forEachIndexed { index, token ->

            if (!expr.isOp(token)) {
                if (expr.isComplex(token)) return emptyList()

                val tempRes = dictionary.getWordDocuments(token)
                stack.push(tempRes)

            } else {

                when (token) {
                    "AND" -> {
                        if (stack.size < 2) return emptyList()
                        val list1 = stack.pop()
                        val list2 = stack.pop()

                        val tempRes = list1.intersect(list2)
                        stack.push(tempRes.toList())
                    }
                    "NOT" -> {
                        if (stack.size < 1) return emptyList()
                        val list1 = stack.pop()
                        val tempRes = mutableListOf<Int>()
                        for (i in 0..(fileQuantity - 1)) {
                            if (!list1.contains(i)) {
                                tempRes.add(i)
                            }
                        }
                        stack.push(tempRes)
                    }
                    "OR" -> {
                        if (stack.size < 2) return emptyList()
                        val list1 = stack.pop()
                        val list2 = stack.pop()

                        val tempRes = list1.union(list2)
                        stack.push(tempRes.toList())
                    }
                    else -> return emptyList()
                }
            }

        }
        return stack.last()
    }
}
