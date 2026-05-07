package com.mkarshnas6.karenstudio.worldskill.utils.storage

import android.content.ContentUris
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.annotation.RequiresApi

class ScopedStorageManager(private val context: Context) {

    fun getAppSpecificPaths(): Map<String, String> {
        return mapOf(
            // internal
            "internal_files" to context.filesDir.absolutePath,
            "internal_cache" to context.cacheDir.absolutePath,
            // external
            "external_files" to (context.getExternalFilesDir(null)?.absolutePath ?: "ندارد"),
            "external_cache" to (context.externalCacheDir?.absolutePath ?: "ندارد")
        )
    }

    // save to gallery
    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveImageToGallery(
        displayName: String,
        imageData: ByteArray
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, displayName)
            put(MediaStore.Images.Media.CONTENT_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES)
            put(MediaStore.Images.Media.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val imageCollection = MediaStore.Images.Media.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
        val uri = resolver.insert(imageCollection, contentValues)
        uri?.let { imageUri ->
            resolver.openOutputStream(imageUri)?.use { outputStream ->
                outputStream.write(imageData)
            }
            contentValues.put(MediaStore.Images.Media.IS_PENDING, 0)
            resolver.update(imageUri, contentValues, null, null)
        }
        return uri
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun saveTextToDocuments(
        fileName: String,
        content: String
    ): Uri? {
        val contentValues = ContentValues().apply {
            put(MediaStore.Downloads.DISPLAY_NAME, fileName)
            put(MediaStore.Downloads.CONTENT_TYPE, "text/plain")
            put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOCUMENTS)
            put(MediaStore.Downloads.IS_PENDING, 1)
        }
        val resolver = context.contentResolver
        val collection = MediaStore.Downloads.getContentUri(
            MediaStore.VOLUME_EXTERNAL_PRIMARY
        )
        val uri = resolver.insert(collection, contentValues)
        uri?.let { textUri ->
            resolver.openOutputStream(textUri)?.use { writer ->
                writer.write(content.toByteArray())
            }

            contentValues.clear()
            contentValues.put(MediaStore.Downloads.IS_PENDING, 0)
            resolver.update(textUri, contentValues, null, null)
        }
        return uri
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    fun getAllImages(): List<Uri> {
        val images = mutableListOf<Uri>()
        val projection = arrayOf(
            MediaStore.Images.Media._ID,
            MediaStore.Images.Media.DISPLAY_NAME,
            MediaStore.Images.Media.SIZE
        )
        val sortOrder = "${MediaStore.Images.Media.DATE_ADDED} DESC"
        context.contentResolver.query(
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            projection,
            null,
            null,
            sortOrder
        )?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
            while (cursor.moveToNext()) {
                val id = cursor.getLong(idColumn)
                val contentUri = ContentUris.withAppendedId(
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    id
                )
                images.add(contentUri)
            }
        }
        return images
    }

    fun readFileFromUri(uri: Uri): ByteArray? {
        return context.contentResolver.openInputStream(uri)?.use { inputStream ->
            inputStream.readBytes()
        }
    }

    fun deleteFileFromMediaStore(uri: Uri): Boolean {
        val deleted = context.contentResolver.delete(uri, null, null)
        return deleted > 0
    }

}