package preparatory

import java.util.*

class FileKeeper {
    private val list : SortedSet<String> = sortedSetOf()

    operator fun get(index: Int): String {
        return list.elementAt(index)
    }

    fun addFile(file: String) {
        list.add(file)
    }

    fun getFileID(file: String): Int {
        return list.indexOf(file)
    }

    fun quantity(): Int = list.size

}