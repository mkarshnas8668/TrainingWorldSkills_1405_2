package com.mkarshnas6.karenstudio.worldskill.utils

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.BufferedWriter
import java.io.File
import java.io.FileOutputStream
import java.io.FileReader
import java.io.FileWriter
import java.io.IOException

class InternalStorageManager(private val context: Context) {

    private val filesDir: File
        get() = context.filesDir

    private val cachesDir: File
        get() = context.cacheDir

    suspend fun saveTextFile(fileName: String, content: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)
                file.parentFile?.mkdirs()

                BufferedWriter(FileWriter(file)).use() { writer ->
                    writer.write(content)
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }


    suspend fun saveBinaryFile(fileName: String, data: ByteArray): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)
                FileOutputStream(file).use { outputStream ->
                    outputStream.write(data)
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun appendToFile(fileName: String, content: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)
                BufferedWriter(FileWriter(file, true)).use { writer ->
                    writer.append(content)
                    writer.append("\n")
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun readFileText(fileName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)

                if (!file.exists()) return@withContext ""

                BufferedReader(FileReader(file)).use { reader ->
                    reader.readText()
                }
            } catch (e: IOException) {
                e.printStackTrace()
                ""
            }
        }
    }

    suspend fun readFileLines(fileName: String): List<String> {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)
                if (!file.exists()) return@withContext emptyList()

                file.bufferedReader().use { reader -> reader.readLines() }

            } catch (e: IOException) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun readBinaryFiles(fileName: String): ByteArray? {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)
                if (!file.exists()) return@withContext null
                file.readBytes()
            } catch (e: IOException) {
                e.printStackTrace()
                null
            }
        }
    }

    suspend fun deleteFile(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val file = File(filesDir, fileName)
                file.delete()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun deleteAllFiles(): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                filesDir.listFiles()?.forEach { it.deleteRecursively() }
                true
            } catch (e: Exception) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun listAllFiles(): List<File> {
        return withContext(Dispatchers.IO) {
            try {
                filesDir.walkTopDown().filter { it.isFile }.toList()
            } catch (e: IOException) {
                e.printStackTrace()
                emptyList()
            }
        }
    }

    suspend fun getInfoFile(fileName: String): Map<String, Any> {
        return withContext(Dispatchers.IO) {

            val file = File(filesDir, fileName)
            mapOf(
                "exist" to file.exists(),
                "size" to file.length(), // get size file to bite
                "lastModified" to file.lastModified(),
                "path" to file.absolutePath,
                "canRead" to file.canRead(),
                "canWrite" to file.canWrite()
            )

        }
    }

    suspend fun saveCatchFile(key: String, content: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val catchFile = File(cachesDir, key)
                BufferedWriter(FileWriter(catchFile)).use { writer ->
                    writer.write(content)
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun readCacheFile(fileName: String): String? {
        return withContext(Dispatchers.IO) {
            val fileCatch = File(cachesDir, fileName)
            if (fileCatch.exists()) fileCatch.readText() else null
        }
    }

    suspend fun clearCacheFiles(): Boolean {
        return withContext(Dispatchers.IO) {
            cachesDir.deleteRecursively()
        }
    }

}