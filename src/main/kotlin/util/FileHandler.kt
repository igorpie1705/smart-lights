package util;

import java.io.File;

object FileHandler {
    fun readFile(filePath: String): String {
        return File(filePath).readText()
    }

    fun writeFile(filePath: String, data: String) {
        File(filePath).writeText(data)
    }
}
