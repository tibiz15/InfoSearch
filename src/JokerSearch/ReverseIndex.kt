package JokerSearch

import booleanSearch.InvertedIndex
import org.apache.commons.collections4.trie.PatriciaTrie

class ReverseIndex : InvertedIndex() {

    private val reverseData = PatriciaTrie<WordData>()

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

            reverseData[word.reversed()] = index
        } else {
            value.occurences++
            value.documents.add(docId)

            val reverseValue = reverseData[word.reversed()]
            if (reverseValue != null) {
                reverseValue.occurences++
                reverseValue.documents.add(docId)
            }
        }

    }

    override fun printWords() {
        println("words:")
        super.printWords()
        println("reversed:")
        reverseData.forEach {
            println("${it.key} ${it.value}")
        }
    }

}