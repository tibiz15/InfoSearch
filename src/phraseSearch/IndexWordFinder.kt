package phraseSearch

import booleanSearch.DocumentWordFinder
import booleanSearch.InvertedIndex

interface IndexWordFinder : DocumentWordFinder {

    override fun addWord(word: String, docID: Int) {

    }

}