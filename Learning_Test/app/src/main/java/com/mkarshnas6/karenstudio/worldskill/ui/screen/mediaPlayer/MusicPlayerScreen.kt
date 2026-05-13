package com.mkarshnas6.karenstudio.worldskill.ui.screen.mediaPlayer

import android.content.Context
import android.media.MediaPlayer
import android.widget.Toast
import androidx.compose.foundation.layout.Arrangement
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
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R
import kotlinx.coroutines.delay

@Composable
fun MusicPlayerScreen(
    navController: NavController,
    context: Context
) {
    // get media in storage
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }

    // status of playing
    var isPlaying by remember { mutableStateOf(false) }

    // variable for change position playing
    // the position plaything music of milli second
    var currentPosition by remember { mutableStateOf(0f) }
    // all duration music
    var duration by remember { mutableStateOf(0f) }
    // all value to slide 0 to 100
    var sliderPosition by remember { mutableStateOf(0f) }
    // for knowing user get slider or no
    var isUserSeeking by remember { mutableStateOf(false) }

    // timer for updating current position
    LaunchedEffect(isPlaying) {
        while (isPlaying) {
            // get now position
            mediaPlayer?.let { mp ->
                currentPosition = mp.currentPosition.toFloat()

                if (!isUserSeeking) {
                    duration = mp.duration.toFloat()
                    if (duration > 0) {
                        sliderPosition = (currentPosition / duration) * 100
                    }
                }
            }
            delay(100)
        }
    }

    // when close page delete media
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayer?.release()
            mediaPlayer = null
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Button(
                onClick = {
                    if (mediaPlayer == null) {
                        mediaPlayer = MediaPlayer.create(context, R.raw.divane_miraghsad)
                        mediaPlayer?.setOnCompletionListener {
                            isPlaying = false
                            it.release()
                            mediaPlayer = null
                        }
                    }
                    mediaPlayer?.start()
                    isPlaying = true
                }
            ) { Text("Play") }

            Spacer(modifier = Modifier.height(16.dp))

            Button(onClick = {
                mediaPlayer?.pause()
                isPlaying = false
            }) { Text("Pause") }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    mediaPlayer?.stop()
                    mediaPlayer?.release()
                    mediaPlayer = null
                    isPlaying = false
                }
            ) { Text("Stop") }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = if (isPlaying) "... در حال پخش" else "متوقف شده"
            )
        }
        Column(modifier = Modifier.padding(horizontal = 30.dp)) {
            Slider(
                value = sliderPosition,
                onValueChange = { sliderState ->
                    sliderPosition = sliderState
                    isUserSeeking = true
                },
                onValueChangeFinished = {
                    mediaPlayer?.let { mp ->
                        val seekTime = ((sliderPosition / 100f) * mp.duration).toInt()
                        mp.seekTo(seekTime)
                    }
                    isUserSeeking = false
                },
                valueRange = 0f..100f,
                modifier = Modifier.fillMaxWidth(),
                colors = SliderDefaults.colors(
                    thumbColor = Color.Red,
                )
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(text = formatTime(currentPosition.toLong()))
                Text(text = formatTime(duration.toLong()))
            }
        }
    }

}

fun formatTime(millis: Long): String {
    val totalSeconds = millis / 1000
    val minutes = totalSeconds / 60
    val seconds = minutes % 60
    return "%02d:%02d".format(minutes, seconds)
}