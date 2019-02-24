package booleanSearch

import expr.Expression
import expr.ExpressionHandler
import phraseSearch.IndexWordFinder

interface SearchEngine {

    var expression : Expression

    var expressionHandler : ExpressionHandler

    private fun loadQuery(query: String) {
        expression = expressionHandler.getExpression(query)
    }

    fun loadDictionary(d: DocumentWordFinder, f: Int) //documentwordfinder as new type

}