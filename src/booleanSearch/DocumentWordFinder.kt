package booleanSearch

interface DocumentWordFinder{

    fun getWordDocuments(word: String): List<Int>

    fun addWord(word: String, docID: Int)

}