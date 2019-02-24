package expr

import java.util.*

class ExpressionHandler {


    private var complexQueries = arrayListOf<String>()

    private fun processExpression(expression: String): String? {

        complexQueries.clear()
        if (expression.count { it == '"' } % 2 == 1) return null

        var expression = expression.trim()
        expression = expression.replace("\"", " \" ")
        expression = expression.replace("'", " \" ")
        expression = expression.replace("(", " ( ")
        expression = expression.replace(")", " ) ")

        var pos1: Int
        var pos2 = 0
        while (expression.contains('"')) {
            pos1 = expression.indexOf('"', pos2 + 1)
            pos2 = expression.indexOf('"', pos1 + 1)

            if (pos1 < 0 || pos2 < 0 || pos1 > pos2 || pos2 > expression.length) break
            val substr = expression.substring(pos1, pos2 + 1)
            expression = expression.replaceRange((pos1 + 1)..(pos2 - 1), complexQueries.size.toString())
            pos2 = pos1 + complexQueries.size.toString().length + 1
            complexQueries.add(substr)
        }

        return expression
    }


    //around range words can't cantain *
    private fun convertToRPN(expression: String): String? {

        val expression: String = processExpression(expression) ?: return null

        var res = ""
        val operations = Stack<String>()
        var lastWord = ""

        for (word in expression.split("\\s+".toRegex())) {

            when {
                ops.containsKey(word) || word.startsWith("RANGE#") -> {



                    if (unary.contains(word) && unary.contains(lastWord)) return null

                    if (unary.contains(word) && (lastWord.isNotEmpty() && !binary.contains(lastWord) && !lastWord.startsWith(
                            "RANGE#"
                        ))
                    ) return null



                    if (
                        (ops.contains(lastWord) || lastWord.startsWith("RANGE#") || lastWord == "(") &&
                        (binary.contains(word) || word.startsWith("RANGE#"))

                    ) return null




                    if ((rawWords.contains(word) || word.startsWith("RANGE#")) && (lastWord == ")" || ops.containsKey(
                            lastWord
                        ) || lastWord.startsWith("RANGE#"))
                    ) return null


                    //if((lastWord == ")" || isComplex(lastWord) ) && word.startsWith("RANGE#")) return null

                    if (operations.empty() || operations.peek() == "(") {
                        operations.push(word)
                    } else {
                        while (!operations.empty()) {
                            if (operations.peek() == "(") break
                            if (unary.contains(word)) break

                            val priority1: Int = if (word.startsWith("RANGE#")) {
                                10
                            } else {
                                ops.getValue(word)
                            }
                            val priority2: Int = if (operations.peek().startsWith("RANGE#")) {
                                10
                            } else {
                                ops.getValue(operations.peek())
                            }

                            if (priority1 <= priority2) {
                                res += operations.pop() + " "
                            } else {
                                break
                            }
                        }
                        operations.push(word)

                    }
                }
                word == "(" -> {



                    if (!lastWord.isEmpty() && !ops.containsKey(lastWord) && lastWord != "(") return null
                    if ((rawWords.contains(lastWord) || lastWord.startsWith("RANGE#")) && (word == "(" || ops.containsKey(
                            word
                        ) || word.startsWith("RANGE#"))
                        //check if before the "(" ther is operator if not then error
                        //also check if next word after range is not "complex query"
                        //and if not q*ry
                    ) return null

                    if(isComplex(lastWord) && word == "(") return null

                    operations.push("(")

                }
                word == ")" -> {
                    if (lastWord.isEmpty() && (ops.containsKey(lastWord) || word == "(")) return null
                    while (!operations.empty()) {
                        if (operations.peek() == "(") break
                        res += operations.pop() + " "
                    }
                    if (operations.peek() != "(") return null
                    operations.pop()
                }
                else -> {

                    //if(isComplex(lastWord))  return null
                    //if(lastWord != "(" && lastWord != ")" && !lastWord.startsWith("RANGE#") && !isOp(lastWord) && isComplex(word)) return null
                    //if(isComplex(lastWord) && isComplex(word)) return null

                    res += "$word "
                }
            }
            lastWord = word
        }

        while (!operations.empty()) {
            val r = operations.pop()
            if (!ops.containsKey(r) && !r.startsWith("RANGE#")) return null
            res += "$r "
        }

        return res
    }

    fun getExpression(query: String): Expression {
        val exp = Expression()
        val res = convertToRPN(query)
        if (res == null) {
            exp.body = ""
            exp.errors = true
        } else {
            exp.body = res.trim()
            exp.errors = false
        }
        exp.complexQueries = complexQueries
        return exp
    }
}