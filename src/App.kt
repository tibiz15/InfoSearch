import JokerSearch.JokerSearch
import JokerSearch.PermutermIndex
import JokerSearch.ReverseIndex
import JokerSearch.TrigramIndex
import booleanSearch.BooleanSearch
import booleanSearch.InvertedIndex
import expr.ExpressionHandler
import jdk.nashorn.internal.ir.ExpressionStatement
import phraseSearch.BiWordIndex
import phraseSearch.PhraseSearch
import phraseSearch.PositionIndex
import preparatory.FileKeeper
import preparatory.Loader
import preparatory.Tokenizer
import spimi.SPIMI
import javax.print.attribute.standard.MediaSize
import kotlin.system.measureTimeMillis
import com.sun.xml.internal.ws.streaming.XMLStreamReaderUtil.close
import compress.CompressedDictionary
import compress.CompressedIndex
import dictionary.Dictionary
import jdk.nashorn.internal.ir.debug.ObjectSizeCalculator
import spimi.SPIMIalg
import java.io.*
import java.lang.instrument.Instrumentation


fun measureTime(action: () -> Unit) {
    val time = measureTimeMillis {
        action()
    }
    val minutes = time / 1000 / 60
    val seconds = time / 1000 % 60

    println("$minutes minutes $seconds seconds $time")
}


fun maine() {


    val loader = Loader()
    val splitter = Tokenizer()
    val dictionary = Dictionary()
    val files = FileKeeper()

    measureTime {


        loader.loadDir("data/txtBig/") { fileName: String, contents: String ->
            files.addFile(fileName)
            val docID = files.getFileID(fileName)
            var wordIndex = 0
            splitter.splitText(contents) {
                dictionary.addWord(it)//, docID,wordIndex)
                wordIndex++
            }


        }
    }


    println("${dictionary.totalWordCount()} words")
    println("${dictionary.uniqueWordCount()} unique words")


    /*val engine = JokerSearch(dictionary, files.quantity())

    while (true) {
        println("=================")
        println("Enter your query(complex query only):")
        val query = readLine()!!

        measureTime {

            val res = engine.performQuery(query).sorted()
            for (file in res) {
                println(files[file])
            }
        }
    }*/
    dictionary.save("data/out/dictionary/dictionary.dct")
}

fun main(){

    //SPIMIalg("data/txtBig/")

    val loader = Loader()
    val splitter = Tokenizer()
    val dictionary = InvertedIndex()
    val files = FileKeeper()

    measureTime {


        loader.loadDir("data/txt/") { fileName: String, contents: String ->
            files.addFile(fileName)
            val docID = files.getFileID(fileName)
            var wordIndex = 0
            splitter.splitText(contents) {
                dictionary.addWord(it, docID)//,wordIndex)
                wordIndex++
            }


        }
    }

    println("${dictionary.totalWordCount()} words")
    println("${dictionary.uniqueWordCount()} unique words")

    //var test = CompressedIndex(dictionary)
    //var test = CompressedDictionary("data/comp/dicitonary.cmp")
    var test = CompressedIndex("data/comp/index.cmp")
    //test.printWords()

    //println(ObjectSizeCalculator.getObjectSize(dictionary))
    //println(ObjectSizeCalculator.getObjectSize(test))

    println(test.wordExists("review"))

    //test.save("data/comp/index2.cmp")

    println(test.getWordDocuments("parrot"))
}