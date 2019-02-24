package booleanSearch

import dictionary.WordKeeper
import kotlin.collections.ArrayList

class IncidentMatrix : WordKeeper, DocumentWordFinder {

    constructor()

    /*constructor(fileCount: Int, wordCount: Int) {
        ///emplace size
    }*/

    private val matrix: MutableMap<String, MutableList<Byte>> = mutableMapOf()

    private var totalWordCount = 0

    override fun totalWordCount() = totalWordCount

    override fun uniqueWordCount() = matrix.size

    override fun wordExists(word: String) = matrix.containsKey(word)

    override fun save(filePath: String) {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun printWords() {
        matrix.forEach {
            println("${it.key} ${it.value}")
        }
    }

    override fun getWordDocuments(word: String): List<Int> {
        val res = mutableListOf<Int>()
        matrix[word]!!.forEachIndexed<Byte> { i: Int, byte: Byte ->
            if (byte.toInt() != 0) {
                res.add(i)
            }
        }
        return res
    }

    override fun addWord(word: String, docID: Int) {
        totalWordCount++
        if (matrix.containsKey(word)) {
            if (matrix[word] == null) return

            if (matrix[word]!!.size <= docID) {
                matrix[word]!!.add(1)
            } else {
                matrix[word]!![docID] = 1
            }


        } else {

            matrix.forEach {
                if (it.value.size <= docID) {
                    it.value.add(0)
                } else if (it.value[docID].toInt() == 0) {
                    it.value[docID] = 0
                }
            }

            val byteArr = ByteArray(docID + 1) { it ->
                0
            }
            byteArr[docID] = 1
            val list = ArrayList<Byte>(byteArr.toMutableList())
            matrix.put(word, list)
        }

    }

}