package booleanSearch

import dictionary.WordKeeper
import org.apache.commons.collections4.trie.PatriciaTrie
import java.io.File

open class InvertedIndex : WordKeeper, DocumentWordFinder {

    class WordData {
        var occurences = 0
        val documents: MutableSet<Int> = mutableSetOf()

        override fun toString(): String {
            return "[occurences=$occurences, documents=$documents]"
        }

    }

    fun getData():Map<String,WordData> {
        return data.toSortedMap()
    }

    protected val data = PatriciaTrie<WordData>()

    protected var totalWordCount = 0

    override fun uniqueWordCount() = data.size

    override fun totalWordCount() = totalWordCount

    override fun wordExists(word: String) = data.contains(word)

    override fun addWord(word: String, docId: Int) {

        if (word.isEmpty() || word.isBlank()) return
        if (word.length == 1 && !word[0].isDigit() && !word[0].isLetter()) return
        totalWordCount++
        val value = data[word]
        if (value == null) {
            val index = WordData()
            index.occurences = 1
            index.documents.add(docId)
            data[word] = index
        } else {
            value.occurences++
            value.documents.add(docId)
        }
    }

    override fun printWords() {
        data.forEach {
            println("${it.key} ${it.value}")
        }
    }

    override fun save(filePath: String) {
        val writer = File(filePath).bufferedWriter()
        data.forEach {
            writer.append("${it.key} ${it.value.occurences} ${it.value.documents}\n")
        }
        writer.close()
    }


    override fun getWordDocuments(word:String):List<Int> {
        val wordInvIndex = data[word] ?: return listOf()
        val res = wordInvIndex.documents
        return res.toList()
    }

}