package compress

import booleanSearch.DocumentWordFinder
import booleanSearch.InvertedIndex
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
import preparatory.Loader
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class CompressedIndex : DocumentWordFinder {
    override fun getWordDocuments(word: String): List<Int> {

        var l = 0
        var r = pointers.size - 1
        var x = word


        while (l <= r) {
            var m = l + (r - l) / 2;

            // Check if x is present at mid
            if (getWord(m) == "") return emptyList()

            if (getWord(m) == x) {

                val offsets = pointers[m].second

                var docs = mutableListOf<Int>()

                val str = String(offsets)
                println(str)

                var saved = ""


                for (i in 0 until (str.length / 8)) {

                    var tempStr = str.substring((i * 8), (i + 1) * 8)

                    if(tempStr[0] == '0') {
                        saved += tempStr
                    }
                    else if (tempStr[0] == '1') {

                        saved += "0" + tempStr.substring(1)
                        val num = saved.toInt(2)
                        docs.add(num)
                        saved = ""
                    }
                }

                val docList = mutableListOf<Int>()
                var index = 0
                docs.forEach {
                    docList.add(it + index)
                    index += it
                }

                return docList

            }

            // If x greater, ignore left half
            if (getWord(m) < x)
                l = m + 1;

            // If x is smaller, ignore right half
            else
                r = m - 1;
        }

        // if we reach here, then element was
        // not present
        return emptyList()


    }

    override fun addWord(word: String, docID: Int) {
        //do nothing as you are not supposed to add words into the compressed index
        println("NOT ADDING WORDS INTO COMPRESSED INDEX")
    }

    fun totalWordCount() = totalCount

    fun uniqueWordCount() = pointers.size

    fun wordExists(word: String): Boolean {

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

    private fun compressList(u: List<Int>): ByteArray {

        //just list of doc ID,
        var compressedDocList = mutableListOf<Int>()
        val tempSet = u.toSortedSet()

        var index = 0

        tempSet.forEach {
            compressedDocList.add(it - index)
            index = it

        }

        var str = ""
        compressedDocList.forEach {
            str += toCompressedByteList(it)
        }

        val res = str.toByteArray()

        return res

    }

    private fun toCompressedByteList(num: Int): String {
        var temp = num.toString(2)

        var res = ""
        val size = if (temp.length % 7 == 0) (temp.length / 7) else (temp.length / 7) + 1

        if (size == 1) {

            res = "10000000"
            res = res.substring(0, (8 - temp.length))
            res += temp

        } else if (size > 1) {
            res = "1"
            res += temp.substring(temp.length - 7)
            temp = temp.substring(0, temp.length - 7)

            for (i in 2 until size) {
                var tempStr = "0"
                tempStr += temp.substring(temp.length - 7)
                temp = temp.substring(0, temp.length - 7)
                res = tempStr + res

            }

            var tempStr = "00000000"
            tempStr = tempStr.substring(0, (8 - temp.length))
            tempStr += temp

            res = tempStr + res

        }


        return res

    }

    private fun getWord(index: Int): String {

        if (index == pointers.size - 1) return data.substring(pointers[index].first)
        val l = pointers[index].first
        val r = pointers[index + 1].first

        return data.substring(l, r)
    }

    private var data = ""

    private var totalCount = 0

    //             positions of words  <=>  list of compressed files indexes
    private var pointers = mutableListOf<Pair<Int, ByteArray>>()


    //able to construct from Dictionary or InvertedIndex
    constructor(dictionary: InvertedIndex) {

        val uncompressedData = dictionary.getData()

        var pos = 0

        uncompressedData.forEach { t, u ->
            //make list here

            val compressedDocList = compressList(u.documents.toList())

            data += t
            pointers.add(Pair(pos, compressedDocList))
            totalCount += u.occurences
            pos += t.length
        }

    }

    constructor(filePath: String) {
        load(filePath)
    }

    fun printWords() {
        println("Data:")
        println(data)
        println("Pointers:")
        println(pointers)
    }


    fun save(filePath: String) {
        val writer = File(filePath).bufferedWriter()

        writer.append(data + "\n")

        pointers.forEach {
            //writer.append("${it.first}, ${it.second}\n")
            //val listStr = it.second.toString().removePrefix("[").removeSuffix("]")
            var listStr = String(it.second)//string of 0 and 1s

            val l = listStr.chunked(32)//divided into 32 bit ints

            val docs = l.map {
                it.toInt(2)
            }.toString().removePrefix("[").removeSuffix("]").trim()

            writer.append("${it.first}|${docs}\n")
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

                    val lst = word.split("|")

                    val numList = lst[1].split(",").map {
                        it.trim().toInt()
                    }

                    val strList = numList.map {
                        it.toString(2)
                    }

                    val str = strList.joinToString("") {
                        it
                    }

                    pointers.add(Pair(lst[0].trim().toInt(), str.toByteArray()))

                }
            }


        }
    }
}
