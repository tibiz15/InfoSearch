package spimi

import preparatory.Loader
import java.io.*


fun SPIMIalg(dirName: String) {
    val spimi = SPIMI();

    var fileId = 0;

    val loader = Loader()

    loader.loadDir(dirName) { fileName: String ->
        val r = BufferedReader(InputStreamReader(FileInputStream(fileName)))
        val streamTokenizer = StreamTokenizer(r)

        spimi.SPIMIinvert(streamTokenizer,fileId)
        fileId++
    }
    spimi.flushRemaining()

    println("Flushed,merging!")

    spimi.mergeAllBlocks();

    spimi.saveDictionary();

    println(spimi.dictionary.size)
}
