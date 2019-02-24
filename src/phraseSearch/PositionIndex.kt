package phraseSearch

import booleanSearch.InvertedIndex
import dictionary.WordKeeper
import org.apache.commons.collections4.trie.PatriciaTrie
import java.io.File

open class PositionIndex : WordKeeper, IndexWordFinder {


    class WordData {
        var occurences = 0
        //docID , list of positions in this doc
        val documents: MutableMap<Int, MutableList<Int>> = mutableMapOf()

        override fun toString(): String {
            return "[occurences=$occurences, documents=$documents]"
        }
    }

    private var totalWordCount = 0

    var data = PatriciaTrie<WordData>()

    override fun totalWordCount() = totalWordCount

    override fun uniqueWordCount() = data.size

    override fun wordExists(word: String) = data.contains(word)

    override fun save(filePath: String) {
        val writer = File(filePath).bufferedWriter()
        data.forEach {
            writer.append("${it.key} ${it.value.occurences} ${it.value.documents}\n")
        }
        writer.close()
    }

    override fun printWords() {
        data.forEach {
            println("${it.key} ${it.value}")
        }
    }

    open fun getWordDocumentsAndPos(word: String): WordData {
        val data = data[word] ?: return WordData()
        return data
    }

    override fun getWordDocuments(word: String): List<Int> {
        val wordData = data[word] ?: return listOf()
        val res = wordData.documents.keys
        return res.toList()
    }

    open fun addWord(word: String, docId: Int, pos: Int) {
        if (word.isEmpty() || word.isBlank()) return
        if (word.length == 1 && !word[0].isDigit() && !word[0].isLetter()) return
        totalWordCount++
        val value = data[word]
        if (value == null) {
            val index = WordData()
            index.occurences = 1
            index.documents[docId] = mutableListOf(pos)
            data[word] = index
        } else {
            value.occurences++
            if (value.documents.containsKey(docId)) {
                value.documents[docId]?.add(pos)

            } else {
                value.documents[docId] = mutableListOf(pos)
            }
        }
    }


}