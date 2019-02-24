package phraseSearch

import booleanSearch.InvertedIndex

class BiWordIndex : InvertedIndex(), IndexWordFinder {

    override fun addWord(word: String, docId: Int) {
        if (word.isEmpty() || word.isBlank()) return
        if (word.length == 1 && !word[0].isDigit() && !word[0].isLetter()) return
        var word = word.trim()
        if (!word.contains(" ")) return

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

    fun addWord(word1: String, word2: String, docId: Int) {
        addWord("$word1 $word2", docId)
    }


    //add override for get document for single word

}