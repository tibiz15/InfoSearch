package compress

import booleanSearch.InvertedIndex
import dictionary.Dictionary
import dictionary.WordKeeper
import preparatory.Loader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CompressedDictionary : WordKeeper {
    override fun totalWordCount() = totalCount

    override fun uniqueWordCount() = pointers.size

    override fun wordExists(word: String): Boolean {

        var l = 0
        var r = pointers.size - 1
        var x = word


        while (l <= r) {
            var m = l + (r - l) / 2;

            // Check if x is present at mid
            if (getWord(m) == "") return false

            if (getWord(m) == x)
                return true

            // If x greater, ignore left half
            if (getWord(m) < x)
                l = m + 1;

            // If x is smaller, ignore right half
            else
                r = m - 1;
        }

        // if we reach here, then element was
        // not present
        return false
    }

    fun getWord(index: Int): String {

        if (index == pointers.size - 1) return data.substring(pointers[index].first)
        val l = pointers[index].first
        val r = pointers[index + 1].first

        return data.substring(l, r)
    }

    override fun save(filePath: String) {
        //later

        val writer = File(filePath).bufferedWriter()

        writer.append(data + "\n")

        pointers.forEach {
            writer.append("${it.first}, ${it.second}\n")
        }
        writer.close()

    }

    fun load(filePath: String) {

        val loader = Loader()
        var stream = Files.lines(Paths.get(filePath))

        var index = 0
        stream.forEach { word ->
            if (word != null) {
                if (index == 0) {
                    data = word
                    index++
                } else {

                    val lst = word.split(",")

                    val numList = lst.map {
                        it.trim().toInt()
                    }

                    pointers.add(Pair(numList[0], numList[1]))

                }
            }

        }
    }

    private var data = ""

    private var totalCount = 0

    //             positions of words  <=>  list of compressed files indexes(null if it's froma  simple dictionary
    private var pointers = mutableListOf<Pair<Int, Int>>()


    //able to construct from Dictionary or InvertedIndex
    constructor(dictionary: Dictionary) {

        val uncompressedData = dictionary.getData()

        var pos = 0

        uncompressedData.forEach { t, u ->
            data += t
            pointers.add(Pair(pos, u))
            totalCount += u
            pos += t.length
        }

    }

    constructor(filePath: String) {
        load(filePath)
    }


    override fun printWords() {
        println("Data:")
        println(data)
        println("Pointers:")
        println(pointers)
    }

}
