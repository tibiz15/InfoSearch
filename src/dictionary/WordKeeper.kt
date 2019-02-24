package dictionary

interface WordKeeper {

    fun totalWordCount(): Int

    fun uniqueWordCount(): Int

    fun wordExists(word: String): Boolean

    //vararg added to complement with future realizations which need additional
    //parameters,if you dont need additional parameters,just leave additional
    //fun addWord(word: String, vararg additional: Any)
    //TODO(decide): mb default implementation?

    fun save(filePath: String)

    fun printWords()

}
