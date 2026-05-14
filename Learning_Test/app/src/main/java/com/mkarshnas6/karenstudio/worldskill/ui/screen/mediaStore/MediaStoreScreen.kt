package com.mkarshnas6.karenstudio.worldskill.ui.screen.mediaStore

import android.content.Context
import android.net.Uri
import android.provider.MediaStore
import android.provider.MediaStore.MediaColumns.DATE_ADDED
import android.widget.ImageView
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.net.toUri
import androidx.navigation.NavController

@Composable
fun MediaStoreScreen(
    navController: NavController,
    context: Context
) {
    var mediaList by remember { mutableStateOf(listOf<MediaItem>()) }
    var selectedTab by remember { mutableStateOf(0) } // 0=عکس, 1=ویدیو, 2=آهنگ

    Column(modifier = Modifier.fillMaxSize()) {
        // Tab ها
        Row {
            listOf("عکس‌ها", "ویدیوها", "آهنگ‌ها").forEachIndexed { index, title ->
                Button(
                    onClick = {
                        selectedTab = index
                        mediaList = when (index) {
                            0 -> readImages(context)
                            1 -> readVideos(context)
                            else -> readAudio(context)
                        }
                    }
                ) {
                    Text(title)
                }
            }
        }

        // نمایش لیست
        LazyVerticalGrid(
            columns = GridCells.Fixed(3),
            modifier = Modifier.fillMaxSize()
        ) {
            items(mediaList) { item ->
                // برای عکس و ویدیو thumbnail، برای آهنگ اسم
                if (item.type == MediaItemTypes.AUDIO) {
                    Text(
                        text = item.name ?: "آهنگ",
                        modifier = Modifier.padding(8.dp)
                    )
                } else {
                    AndroidView(
                        factory = { ctx ->
                            ImageView(ctx).apply {
                                setImageURI(item.uri)
                                scaleType = ImageView.ScaleType.CENTER_CROP
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .aspectRatio(1f)
                            .padding(2.dp)
                    )
                }
            }
        }
    }
}

enum class MediaItemTypes {
    IMAGE,
    VIDEO,
    AUDIO
}

// مدل داده
data class MediaItem(
    val uri: Uri,
    val name: String?,
    val type: MediaItemTypes // "image", "video", "audio"
)

// خوندن عکس‌ها
fun readImages(context: Context): List<MediaItem> {
    val mediaList = mutableListOf<MediaItem>()
    val collection = MediaStore.Images.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(
        collection,
        arrayOf(MediaStore.Images.Media._ID, MediaStore.Images.Media.DISPLAY_NAME),
        null,
        null,
        "${DATE_ADDED} DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            mediaList.add(
                MediaItem(
                    uri = "${collection}/${cursor.getLong(idColumn)}".toUri(),
                    name = cursor.getColumnName(nameColumn),
                    type = MediaItemTypes.IMAGE
                )
            )
        }
    }
    return mediaList
}

// خوندن ویدیوها
fun readVideos(context: Context): List<MediaItem> {
    val mediaList = mutableListOf<MediaItem>()
    val collection = MediaStore.Video.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(
        collection,
        arrayOf(MediaStore.Video.Media._ID, MediaStore.Video.Media.DISPLAY_NAME),
        null,
        null,
        "$DATE_ADDED DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DISPLAY_NAME)
        while (cursor.moveToNext()) {
            mediaList.add(
                MediaItem(
                    uri = "${collection}/${MediaStore.Video.Media._ID}".toUri(),
                    name = cursor.getString(nameColumn),
                    type = MediaItemTypes.VIDEO
                )
            )
        }
    }
    return mediaList
}

// خوندن آهنگ‌ها
fun readAudio(context: Context): List<MediaItem> {
    var mediaList = mutableListOf<MediaItem>()
    var collection = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI

    context.contentResolver.query(
        collection,
        arrayOf(MediaStore.Audio.Media._ID, MediaStore.Audio.Media.DISPLAY_NAME),
        null,
        null,
        "$DATE_ADDED DESC"
    )?.use { cursor ->
        val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
        val nameColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)

        while (cursor.moveToNext()) {
            mediaList.add(
                MediaItem(
                    uri = "$collection/${MediaStore.Audio.Media._ID}".toUri(),
                    name = cursor.getString(nameColumn),
                    type = MediaItemTypes.AUDIO
                )
            )
        }
    }
    return mediaList
}