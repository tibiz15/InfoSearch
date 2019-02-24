package dictionary

import org.apache.commons.collections4.trie.PatriciaTrie
import java.io.File

class Dictionary : WordKeeper {

    private val data = PatriciaTrie<Int>()

    public fun getData():Map<String,Int> {
        return data.toSortedMap()
    }

    private var totalWordCount = 0

    override fun totalWordCount(): Int = totalWordCount

    override fun uniqueWordCount(): Int = data.size

    override fun wordExists(word: String) = data.contains(word)

    //additional parameters are not used,thus we don't touch 'additional'
    fun addWord(word: String) {
        if (word.isEmpty() || word.isBlank()) return
        if (word.length == 1 && !word[0].isDigit() && !word[0].isLetter()) return
        totalWordCount++
        var wordCount = data[word]
        if (wordCount == null) {
            data[word] = 1
            return
        }
        wordCount++
        data[word] = wordCount
    }

    override fun printWords() {
        data.forEach {
            println("${it.key} ${it.value}")
        }
    }

    fun getWordCount(word: String): Int {
        return data[word] ?: return 0
    }

    override fun save(filePath: String) {
        val writer = File(filePath).bufferedWriter()
        data.forEach {
            writer.append("${it.key} ${it.value}\n")
        }
        writer.close()
    }
}