
package JokerSearch

import phraseSearch.IndexWordFinder
import phraseSearch.PositionIndex
import java.util.*

class JokerSearch(private var dictionary: JokerWordFinder, private var fileQuantity: Int) {


    private var expression = expr.Expression()

    private var expressionHandler = expr.ExpressionHandler()

    private fun loadQuery(query: String) {
        expression = expressionHandler.getExpression(query)
    }

    //to keep the fileKeeper up to date
    fun loadDictionary(d: JokerWordFinder, f: Int) {
        dictionary = d
        fileQuantity = f
    }


    private fun notStrictIntersect(list1: List<Int>, list2: List<Int>, diff: Int): List<Int> {

        val diff = diff + 1
        var set = mutableListOf<Int>()

        var i = 0
        var j = 0
        while (i < list1.size && j < list2.size) {
            if (list1[i] < list2[j]) {
                if (list2[j] - list1[i] <= diff) {
                    set.add(list1[i])
                }
                i++
            } else if (list2[j] < list1[i]) {
                if (list1[i] - list2[j] <= diff) {
                    set.add(list1[i])
                }
                j++
            } else {
                j++
                i++
            }
        }
        return set
    }

    private fun strictIntersect(list1: List<Int>, list2: List<Int>, diff: Int): List<Int> {

        val diff = diff + 1
        val list2 = list2.map { it -> it - diff }

        return list1.intersect(list2).toList()
    }

    private fun intersectAll(vararg lists: Set<Int>): Set<Int> {
        if (lists.isEmpty()) return emptySet()

        val res = lists[0].toMutableSet()

        (1 until lists.size).forEach() {
            res.retainAll(lists[it])
        }

        return res
    }

    private fun multiWordQuery(query: String): List<Int> {

        var query = query.trim().removeSurrounding("\"").trim()

        if (query.isEmpty() || query.isBlank()) return emptyList()

        val words = query.split(("\\s+").toRegex())

        if (words.isEmpty()) return emptyList()

        //first get list of all files where they are
        val tempFiles = mutableListOf<Set<Int>>()

        words.forEach {
            tempFiles.add((dictionary as PositionIndex).getWordDocumentsAndPos(it).documents.keys)
        }

        var docs = intersectAll(*tempFiles.toTypedArray())


        if (docs.isEmpty()) return emptyList()

        var docRes = mutableListOf<Int>()//documents where the pos are good


        //for each document

        docs.forEach {doc ->

            var positions = mutableListOf<Set<Int>>()

            val firstWord = (dictionary as PositionIndex).getWordDocumentsAndPos(words[0]).documents[doc]
            (1 until words.size).forEach() {

                if (firstWord != null) {
                    val tempWord = (dictionary as PositionIndex).getWordDocumentsAndPos(words[it]).documents[doc]
                    if (tempWord != null) {
                        positions.add(strictIntersect(firstWord.toList(),tempWord.toList(),it - 1).toSet())
                    }
                }
            }

            val tempPos = intersectAll(*positions.toTypedArray())

            if(tempPos.isNotEmpty()) {
                docRes.add(doc)
            }
            //in the end just make intersection,of all lists in positions
        }

        return docRes
    }

    fun performQuery(query: String): List<Int> {

        loadQuery(query)
        if (expression.body.isEmpty() || expression.body.isBlank() || expression.errors) return emptyList()

        val stack = Stack<Pair<String, List<Int>>>()

        val strs = expression.body.split("\\s+".toRegex())



        for ((index, token) in strs.withIndex()) {


            if (!expr.isOp(token)) {
                if (expr.isComplex(token)) {

                    //println("comaplex")


                    //
                    //var tempRes = intersetcStrict()

                    //when range, put here the position of the first word of the occurance
                    //when using range with this, search for the frist word + quantity of wards omitted
                    //stack.push(tempRes.keys.toList())

                    //get 2 lists
                    //get all files they both have
                    //search in those files the positions of the words


                    //get by position
                    var index = token.removeSurrounding("\"").toInt()

                    val tempRes = multiWordQuery(expression.complexQueries!![index])
                    stack.push(Pair("", tempRes))

                } else {
                    //simply get the documents

                    val tempRes = dictionary.getWordDocuments(token)

                    stack.push(Pair(token, tempRes))
                }
            } else {
                when {
                    token == "AND" -> {
                        if (stack.size < 2) return emptyList()
                        val list1 = stack.pop()
                        val list2 = stack.pop()

                        val tempRes = list1.second.intersect(list2.second)
                        stack.push(Pair("", tempRes.toList()))
                    }
                    token == "NOT" -> {
                        if (stack.size < 1) return emptyList()
                        val list1 = stack.pop()
                        val tempRes = mutableListOf<Int>()
                        for (i in 0..(fileQuantity - 1)) {
                            if (!list1.second.contains(i)) {
                                tempRes.add(i)
                            }
                        }
                        stack.push(Pair("", tempRes))
                    }
                    token == "OR" -> {
                        if (stack.size < 2) return emptyList()
                        val list1 = stack.pop()
                        val list2 = stack.pop()

                        val tempRes = list1.second.union(list2.second)
                        stack.push(Pair("", tempRes.toList()))
                    }
                    token.startsWith("RANGE#") -> {
                        if (stack.size < 2) return emptyList()


                        val list1 = stack.pop()
                        if (list1.first == "") return emptyList()
                        val list2 = stack.pop()
                        if (list2.first == "") return emptyList()

                        if (dictionary is PositionIndex) {
                            var auto1 = (dictionary as PositionIndex).getWordDocumentsAndPos(list1.first)
                            var auto2 = (dictionary as PositionIndex).getWordDocumentsAndPos(list2.first)

                            var diff = token.removePrefix("RANGE#")


                            var tempRes = posRangeIntersect(auto1, auto2, diff.toInt())

                            stack.push(Pair("", tempRes.toList()))

                        } else {
                            return emptyList()
                        }


                        //use word to get positions then compare
                        //and set Pair("",pos)

                        //for now not
                    }
                }
            }
        }


        if (stack.size != 1) return emptyList()
        return stack.last().second



    }

    private fun posRangeIntersect(data1: PositionIndex.WordData, data2: PositionIndex.WordData, diff: Int): List<Int> {

        if (diff < 0) return emptyList()


        var list1 = data1.documents.keys
        var list2 = data2.documents.keys
        var docs = list1.intersect(list2)


        if (docs.isEmpty()) return emptyList()

        val res = mutableListOf<Int>()

        docs.forEach() {

            val pos1 = data1.documents[it] ?: return emptyList()
            val pos2 = data2.documents[it] ?: return emptyList()

            if (notStrictIntersect(pos1, pos2, diff).isNotEmpty()) {
                res.add(it)
            }
        }

        return res
    }


}
