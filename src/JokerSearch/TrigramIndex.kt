package JokerSearch

import phraseSearch.PositionIndex


class TrigramIndex : PositionIndex(),JokerWordFinder {

    private val grams = mutableMapOf<String, MutableList<String>>()

    override fun addWord(word: String, docId: Int, pos: Int) {
        var exists = wordExists(word)

        super<PositionIndex>.addWord(word, docId, pos)

        if (!exists) {

            val newWord = "$$word$"

            if (newWord.length <= 3) {

                val absent = grams.putIfAbsent(newWord, mutableListOf(word))
                if (absent != null) {
                    grams[newWord]!!.add(word)
                }
            } else {

                (0 until newWord.length - 2).forEach { i ->
                    val gram = newWord.substring(i, i + 3)

                    val absent = grams.putIfAbsent(gram, mutableListOf(word))
                    if (absent != null) {
                        grams[gram]!!.add(word)
                    }
                }
            }
        }
    }

    private fun <T> intersectAll(vararg lists: Set<T>): Set<T> {
        if (lists.isEmpty()) return emptySet()
        if (lists.size == 1) return lists[0]

        val res = lists[0].toMutableSet()

        (1 until lists.size).forEach() {
            res.retainAll(lists[it])
        }

        return res
    }


    private fun postFiltration(initialQuery: String, words: List<String>): List<String> {

        val res = mutableListOf<String>()
        val subQueries = ("$" + initialQuery + "$").split("*")


        //println(subQueries)

        words.forEach { word ->


            var index = 0
            var passes = true

            subQueries.forEach { sub ->


                if(sub != "$") {

                    if (sub.startsWith("$")) {

                        val sub2 = sub.removePrefix("$")
                        if (word.indexOf(sub2) == 0) {
                            index += sub2.length
                        } else {
                            passes = false
                        }

                    } else if (sub.endsWith("$")) {

                        val sub2 = sub.removeSuffix("$")
                        if (!word.endsWith(sub2)) {
                            passes = false
                        }

                    } else {

                        if (word.indexOf(sub, index - 1) != -1) {
                            index += sub.length
                        } else {
                            passes = false
                        }
                    }
                }
            }

            if (passes) {
                res.add(word)
            }

        }

        return res

    }

    private fun getQuery(cleanQuery: String): List<String> {

        var tempList: MutableList<String> = mutableListOf()

        val res: Set<String>
        if (cleanQuery.length < 3) {

            grams.filterKeys {
                it.contains(cleanQuery)
            }.forEach { t, u ->
                tempList.addAll(u)
            }
            res = tempList.toSet()
        } else {

            val words = mutableListOf<MutableSet<String>>()
            val tempGrams = mutableListOf<String>()
            (0 until cleanQuery.length - 2).forEach { i ->
                val gram = cleanQuery.substring(i, i + 3)
                tempGrams.add(gram)
            }

            tempGrams.forEachIndexed { index, s ->
                tempList = grams[s] ?: return emptyList()
                if (tempList == null) {
                    return emptyList()
                }
                words.add(tempList.toMutableSet())
            }
            res = intersectAll(*words.toTypedArray())
        }
        return res.toList()
    }

    override fun getWords(word: String): List<String> {
        if (word == "*") {
            return emptyList()
        }

        if (word.contains("*")) {

            //after doing this, mb combine them son not so many IF's

            val newWord = "$$word$"
            var query = newWord.replace("$*", "")
            query = query.replace("*$", "")
            query = query.replace("*", " ")

            val simpleQueries = query.split(" ")

            val smallRess = mutableListOf<Set<String>>()

            for (i in simpleQueries) {
                smallRess.add(getQuery(i).toSet())
            }

            val res = intersectAll(*smallRess.toTypedArray())

            return postFiltration(word, res.toList())

        }
        return listOf(word)
    }

    //make also function get words, which gets all words that have the asterisk

    override fun getWordDocuments(word: String): List<Int> {
        if(word.contains("*")) {

            val words = getWords(word)

            val res = mutableSetOf<Int>()

            words.forEach {
                res.addAll(super.getWordDocuments(it))
            }

            return res.toList()
        }
        return super.getWordDocuments(word)
    }

    override fun getWordDocumentsAndPos(word: String): WordData {
        if(word.contains("*")) {
            /*
            val words = getWords(word)

            val res = mutableSetOf<WordData>()

            words.forEach {
                res.addAll(super.getWordDocumentsAndPos(it))
            }

            return res.toList()*/
            return WordData()
        }
        return super.getWordDocumentsAndPos(word)
    }

    override fun wordExists(word: String): Boolean {
        if (word.contains("*")) {
            //do action here
            return false;
        }
        return super.wordExists(word)
    }

    override fun printWords() {
        println("words:")
        super.printWords()
        println("trigrams:")
        grams.forEach { t, u ->
            println("$t : $u")
        }
    }


}