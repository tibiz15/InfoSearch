package JokerSearch

import phraseSearch.IndexWordFinder

interface JokerWordFinder : IndexWordFinder{

    fun getWords(word: String): List<String>

}