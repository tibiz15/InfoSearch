package preparatory

import com.kursx.parser.fb2.FictionBook
import java.io.File
import java.lang.Exception
import java.nio.charset.Charset

class Loader {

    fun loadDir(dirName: String, fileProcess: (fileName: String, fileContent: String) -> Unit) {
        if (isDir(dirName)) {
            File(dirName).walkTopDown().forEach {
                if (it.isFile) {
                    val content = loadFile(it.path)
                    if (!content.isNullOrEmpty()) {
                        fileProcess(it.path, content)
                    }
                }
            }
        }
    }

    fun loadDir(dirName: String, fileProcess: (fileName: String) -> Unit) {
        if (isDir(dirName)) {
            File(dirName).walkTopDown().forEach {
                if (it.isFile) {
                    fileProcess(it.path)
                }
            }
        }
    }


    fun loadFile(fileName: String, type: FileType = FileType.type_auto): String? {
        if (isFile(fileName)) {
            when (type) {
                FileType.type_auto -> when {
                    getType(fileName) == ".fb2" -> return loadFB2(fileName)
                    getType(fileName) == ".txt" -> return loadTXT(fileName)
                    getType(fileName) == ".epub" -> return null
                }
                FileType.type_fb2 -> return loadFB2(fileName)
                FileType.type_epub -> return null
                FileType.type_word -> TODO()
                else -> return loadTXT(fileName)
            }
        }
        return null
    }

    fun getDirFileCount(dirName: String): Int {
        if (!isDir(dirName)) return 0
        return File(dirName).list().size
    }

    private fun fileExists(fileName: String): Boolean {
        return File(fileName).exists()
    }

    private fun isFile(fileName: String): Boolean {
        return File(fileName).isFile
    }

    private fun isDir(fileName: String): Boolean {
        return File(fileName).isDirectory
    }


    private fun getType(fileName: String): String {
        val index = fileName.lastIndexOf('.')
        return fileName.takeLast(fileName.length - index)
    }

    private fun loadFB2(fileName: String): String? {
        var str = ""
        //add also title etc
        val fb2: FictionBook
        try {
            fb2 = FictionBook(File(fileName))
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        for (section in fb2.body.sections) {
            for (element in section.elements) {
                str += element.text + "\n"
            }
        }
        return str
    }

    private fun loadTXT(fileName: String, charset: Charset = Charsets.UTF_8): String? {
        var str = ""
        try {
            str = File(fileName).inputStream().readBytes().toString(charset)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
        return str
    }
}