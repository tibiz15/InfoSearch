package JokerSearch

import booleanSearch.InvertedIndex

class PermutermIndex : InvertedIndex() {

    private var mappings = mutableMapOf<String,String>()

    override fun addWord(word: String, docId: Int) {
        super.addWord(word, docId)

        var token = word + "$"
        mappings.put(token,word)

        for( i in token) {
            token = token.substring(1) + token[0]
            mappings.put(token,word)
        }

    }

    override fun printWords(){
        println("words:")
        super.printWords()
        println("mappings:")
        mappings.forEach { t, u ->
            println(t + " = " + u)
        }
    }

}