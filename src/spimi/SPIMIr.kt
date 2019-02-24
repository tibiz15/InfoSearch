package spimi

import preparatory.Loader
import sun.management.snmp.jvminstr.JvmThreadInstanceEntryImpl.ThreadStateMap.Byte1.other
import java.io.File
import java.io.StreamTokenizer
import java.nio.file.Files
import java.nio.file.Paths
import java.util.*
import kotlin.collections.HashMap
import java.util.LinkedHashMap


class SPIMI(private var memorySize: Int = -1) {

    var blockNum = 0
    var dictionary: Map<String, List<Int>> = mutableMapOf()

    private fun sortAndSave(block: Map<String, List<Int>>) {


        val keys = block.keys.toList()
        Collections.sort(keys)

        val lines = ArrayList<String>()
        for (key in keys) {
            Collections.sort(block[key]) //sorting the postings list
            val index = key + " : " + block[key].toString()
            lines.add(index)
        }

        val writer = File("data/out/blocks/block" + blockNum + ".blck").bufferedWriter()
        lines.forEach {
            writer.append("$it\n")
        }
        writer.close()
        println("Saved $blockNum block!")
        blockNum++

    }

    private fun loadBlock(path: String): Map<String, List<Int>> {
        var block: MutableMap<String, MutableList<Int>> = mutableMapOf()

        val loader = Loader()
        var stream = Files.lines(Paths.get(path))

        stream.forEach { word ->
            if (word != null) {
                val term = word.split(":")[0].trim()
                var postingsRaw = word.split(":")[1].trim()
                postingsRaw = postingsRaw.substringAfter("[").substringBefore("]")
                postingsRaw.replace(" ", "")

                val strPostings = postingsRaw.split(",")

                val postings = mutableListOf<Int>()
                strPostings.forEach {
                    postings.add(it.trim().toInt())
                }

                block.put(term, postings)

            }
        }

        return block
    }

    //fun to flush remainning so we dont forget



    private var tempBlock : MutableMap<String, MutableList<Int>> = mutableMapOf()

    fun flushRemaining() {
        if(tempBlock.isNotEmpty()) {
            sortAndSave(tempBlock)
            tempBlock.clear()
        }
    }

    fun SPIMIinvert(file: StreamTokenizer, docId: Int) {

        val initMem = Runtime.getRuntime().freeMemory()

        var block = tempBlock

        var usedMem = initMem - Runtime.getRuntime().freeMemory()

        while (true) {


            if(Runtime.getRuntime().freeMemory() < Runtime.getRuntime().totalMemory() / 2) {
            //if (usedMem > Runtime.getRuntime().maxMemory() * 0.05) {
                sortAndSave(block)

                block = mutableMapOf()
                tempBlock = mutableMapOf()
                usedMem = 0
                System.gc()
            }

            if (file.nextToken() == StreamTokenizer.TT_EOF) {

                tempBlock = block


                return
            }

            var token = ""//get from stream

            if(file.ttype == StreamTokenizer.TT_WORD) {
                token = file.sval;
            }
            else if(file.ttype == StreamTokenizer.TT_NUMBER) {
                continue
            }

            if (!block.containsKey(token)) {
                block.put(token, mutableListOf(docId))
            } else {
                val list = block[token]
                if (list != null) {
                    if(list.last() != docId) {
                        list.add(docId)
                    }
                }
            }

            val currentMem = Runtime.getRuntime().freeMemory()
            usedMem = initMem - currentMem;

        }


    }

    //any way to preserve order?
    //plz
    private fun mergeBlocks(
        block1: Map<String, List<Int>>,
        block2: Map<String, List<Int>>
    ): Map<String, List<Int>> {

        val result = HashMap<String, List<Int>>(block1)
        for ((key, value) in block2) {
            result.merge(key, value) { t: List<Int>, u: List<Int> ->
                t.union(u).toList()
            }
        }
        return result

    }

    fun mergeAllBlocks() {
        var bigBlock: Map<String, List<Int>> = mutableMapOf()

        for (i in 0 until blockNum) {

            var block = loadBlock("data/out/blocks/block" + i + ".blck")

            bigBlock = mergeBlocks(bigBlock, block)
            println("Merged block $i")
        }
        dictionary = bigBlock


    }

    fun saveDictionary() {
        val writer = File("data/out/dictionary/dictionary.dct").bufferedWriter()
        dictionary.forEach {
            writer.append("${it.key} : ${it.value}\n")
        }
        writer.close()
    }

}