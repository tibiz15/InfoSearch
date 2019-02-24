package preparatory

interface WordSplitter {
    fun splitText(text: String, textAction: (word: String) -> Unit)
}