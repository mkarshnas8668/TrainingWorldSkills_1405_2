package com.mkarshnas6.karenstudio.worldskill.utils.storage

import android.content.Context
import android.content.Intent
import android.net.Uri

// storage access framework or SAF
class StorageAccessFramework(private val context: Context) {

    fun createOpenDocumentIntent(
        mimeTypes: Array<String> = arrayOf("*/*") // mean all files
    ): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(if (mimeTypes.size == 1) mimeTypes[0] else "*/*")
            putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }
    }

    fun createCreateDocumentIntent(
        fileName: String = "new_file.txt",
        mimeType: String = "text/plain"
    ): Intent {
        return Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = mimeType
            putExtra(Intent.EXTRA_TITLE, fileName)
        }
    }

    fun createOpenDocumentTreeIntent(): Intent {
        return Intent(Intent.ACTION_OPEN_DOCUMENT_TREE).apply {
            addFlags(
                Intent.FLAG_GRANT_READ_URI_PERMISSION or
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION
            )
        }
    }

    fun readFileFromUri(uri: Uri): String? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.bufferedReader().readText()
        }
    }

    fun writeToUri(uri: Uri, content: String): Boolean {
        return try {
            context.contentResolver.openOutputStream(uri, "wt")?.use { outputStream ->
                outputStream.write(content.toByteArray())
            }
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }



}