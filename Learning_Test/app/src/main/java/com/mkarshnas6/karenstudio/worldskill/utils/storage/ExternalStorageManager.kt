package com.mkarshnas6.karenstudio.worldskill.utils.storage

import android.content.Context
import android.os.Environment
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException

class ExternalStorageManager(private val context: Context) {

    private val externalFilesDir: File? // maybe user cut off SD Card
        get() = context.getExternalFilesDir(null)

    private val externalCachesDir: File?
        get() = context.externalCacheDir

    fun isExternalStorageWritable(): Boolean {
        return Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED // can write external storage
    }

    fun isExternalStorageReadable(): Boolean {
        val state = Environment.getExternalStorageState()
        return state == Environment.MEDIA_MOUNTED || state == Environment.MEDIA_MOUNTED_READ_ONLY
    }

    suspend fun saveToExternalStorage(filename: String, content: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                if (!isExternalStorageWritable()) return@withContext false
                val dir = externalFilesDir ?: return@withContext false
                if (!dir.exists()) dir.mkdirs()
                val file = File(dir, filename)
                file.bufferedWriter().use { writer ->
                    writer.write(content)
                }
                true
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun readFromExternalStorage(fileName: String): String {
        return withContext(Dispatchers.IO) {
            try {
                if (!isExternalStorageReadable()) return@withContext ""
                val dir = externalFilesDir ?: return@withContext ""
                val file = File(dir, fileName)
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

    suspend fun listExternalFiles(): List<File> {
        return withContext(Dispatchers.IO) {
            externalFilesDir?.listFiles()?.toList() ?: emptyList()
        }
    }

    suspend fun deleteExternalFiles(fileName: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                val dir = externalFilesDir ?: return@withContext false
                val file = File(dir, fileName)
                file.delete()
            } catch (e: IOException) {
                e.printStackTrace()
                false
            }
        }
    }

    suspend fun getExternalStorageSpace(): Pair<Long, Long> {
        return withContext(Dispatchers.IO) {
            val dir = externalFilesDir ?: return@withContext Pair(0L, 0L)
            val total = dir.totalSpace
            val freeSpace = dir.freeSpace
            return@withContext Pair(total, freeSpace)
        }
    }

}