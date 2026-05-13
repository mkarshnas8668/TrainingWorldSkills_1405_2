package com.mkarshnas6.karenstudio.worldskill.ui.screen.mediaPlayer

import android.content.Context
import android.net.Uri
import android.widget.VideoView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R
import com.mkarshnas6.karenstudio.worldskill.utils.DataStoreManager
import kotlinx.coroutines.delay

@Composable
fun VideoPlayerScreen(
    navController: NavController,
    context: Context
) {

    val dataStoreManager = remember { DataStoreManager(context = context) }
    // get state video view
    var videoView by remember { mutableStateOf<VideoView?>(null) }
//    state play
    var isPlaying by remember { mutableStateOf(false) }

    var currentPosition by remember { mutableStateOf(0f) }
    var durationVideo by remember { mutableStateOf(0f) }
    var isUserSeeking by remember { mutableStateOf(false) }
    var sliderPosition by remember { mutableStateOf(0f) }

    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            videoView?.let { videoView ->
                currentPosition = videoView.currentPosition.toFloat()
                if (!isUserSeeking) {
                    durationVideo = videoView.duration.toFloat()
                    if (durationVideo > 0) {
                        sliderPosition = ((currentPosition / durationVideo) * 100f)
                        dataStoreManager.saveString("LastVisit", sliderPosition.toString())
                    }
                }
            }
            delay(100)
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            videoView?.pause()
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AndroidView(
                factory = { ctx ->
                    VideoView(ctx).apply {
                        setVideoURI(Uri.parse("android.resource://${ctx.packageName}/${R.raw.gol_ronaldo}"))
                        // when ready video
                        setOnPreparedListener { mp ->
                            mp.isLooping = false
                            videoView = this
                        }
                        // when done play
                        setOnCompletionListener { isPlaying = false }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
            )

            LaunchedEffect(Unit) {
                sliderPosition = dataStoreManager.readString("LastVisit", "0").toFloat()
            }

            Spacer(modifier = Modifier.height(16.dp))

            // control buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // play
                Button(
                    onClick = {
                        videoView?.let {
                            it.seekTo(((sliderPosition / 100f) * it.duration).toInt())
                        }
                        videoView?.start()
                        isPlaying = true
                    }
                ) {
                    Text("Play")
                }

                //pause
                Button(
                    onClick = {
                        videoView?.pause()
                        isPlaying = false
                    }
                ) {
                    Text("Pause")
                }

                //stop
                Button(
                    onClick = {
                        videoView?.pause()
                        videoView?.seekTo(0)
                        isPlaying = false
                    }
                ) {
                    Text("Stop")
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = if (isPlaying) "... در حال پخش" else "متوقف شده"
                )

            }

        }
        Box(Modifier.fillMaxSize()) {
            Slider(
                value = sliderPosition,
                onValueChange = { newValue ->
                    sliderPosition = newValue
                    isUserSeeking = true
                },
                onValueChangeFinished = {
                    videoView?.let { vp ->
                        val seekTime = ((sliderPosition / 100f) * vp.duration).toInt()
                        vp.seekTo(seekTime)
                    }
                    isUserSeeking = false
                },
                valueRange = 0f..100f,
                modifier = Modifier
                    .padding(horizontal = 26.dp, vertical = 16.dp)
                    .fillMaxWidth()
                    .align(Alignment.BottomCenter),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Red
                )
            )
        }

    }

}