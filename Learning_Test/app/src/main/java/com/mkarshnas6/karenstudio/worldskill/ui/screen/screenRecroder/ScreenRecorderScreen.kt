package com.mkarshnas6.karenstudio.worldskill.ui.screen.screenRecroder

import android.app.Activity
import android.content.Context
import android.hardware.display.DisplayManager
import android.media.MediaRecorder
import android.media.projection.MediaProjection
import android.media.projection.MediaProjectionManager
import android.net.Uri
import android.os.Build
import android.util.DisplayMetrics
import android.view.WindowManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import java.io.File

@Composable
fun ScreenRecorderScreen(
    navController: NavController,
    context: Context
) {
    // get services
    val mediaProjectionManager =
        context.getSystemService(Context.MEDIA_PROJECTION_SERVICE) as MediaProjectionManager
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    // state
    var isRecording by remember { mutableStateOf(false) }
    var mediaProjection by remember { mutableStateOf<MediaProjection?>(null) }
    var mediaRecorder by remember { mutableStateOf<MediaRecorder?>(null) }

    // record variable
    val displayMetrics = remember { DisplayMetrics() }
    val videoUri by remember { mutableStateOf<Uri?>(null) }

    // permissions launcher
    val projectionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK && result.data != null) {
            // user have permission for record screen
            val mediaProjection = mediaProjectionManager.getMediaProjection(
                result.resultCode,
                result.data!!
            )
            // call fun start record
            startRecording(context, mediaProjection!!) { recorder ->
                mediaRecorder = recorder
                isRecording = true
            }
        }
    }

    // clear when close screen
    DisposableEffect(Unit) {
        onDispose {
            stopRecording(mediaRecorder,mediaProjection)
        }
    }

//    very simple UI
    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Button(
            onClick = {
                if (isRecording) {
                    // توقف ضبط
                    stopRecording(mediaRecorder, mediaProjection)
                    isRecording = false
                    mediaRecorder = null
                    mediaProjection = null
                } else {
                    // شروع ضبط - اول اجازه بگیر
                    projectionLauncher.launch(
                        mediaProjectionManager.createScreenCaptureIntent()
                    )
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isRecording) Color.Red else Color.Blue
            )
        ) {
            Text(if (isRecording) "توقف ضبط" else "شروع ضبط")
        }

        if (isRecording) {
            Spacer(modifier = Modifier.height(16.dp))
            Text("🔴 در حال ضبط صفحه...", color = Color.Red)
        }
    }

}

fun startRecording(
    context: Context,
    mediaProjection: MediaProjection,
    onProjectionReady: (MediaRecorder) -> Unit
) {
    // settings screen
    val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
    val metrics = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
        // for android 11+
        val windowMetrics = windowManager.maximumWindowMetrics
        DisplayMetrics().apply {
            widthPixels = windowMetrics.bounds.width()
            heightPixels = windowMetrics.bounds.height()
        }
    } else {
        // for android -10
        DisplayMetrics().also {
            windowManager.defaultDisplay.getRealMetrics(it)
        }
    }


    val width = metrics.widthPixels
    val heigh = metrics.heightPixels
    val density = metrics.densityDpi

    val recorder = MediaRecorder().apply {
        // resource record screen
        setVideoSource(MediaRecorder.VideoSource.SURFACE)
        // resource sound : microphone
        setAudioSource(MediaRecorder.AudioSource.MIC)
        // format
        setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
        // settings format video
        setVideoEncoder(MediaRecorder.VideoEncoder.H264)
        setVideoSize(width, heigh)
        setVideoFrameRate(30)
        setVideoEncodingBitRate(8 * 1000 * 1000)

        // settings sound
        setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
        setAudioEncodingBitRate(128000)
        setAudioSamplingRate(44100)

        val outputFile = File(
            context.getExternalFilesDir(null),
            "recording_${System.currentTimeMillis()}.mp4"
        )
        setOutputFile(outputFile)
        // ready
        prepare()
    }

    // create virtual display : very important
    mediaProjection.createVirtualDisplay(
        "ScreenCapture",
        width,
        heigh,
        density, // تراکم پیکسل
        DisplayManager.VIRTUAL_DISPLAY_FLAG_AUTO_MIRROR, // اینه
        recorder.surface,
        null,
        null
    )

    // start record
    recorder.start()
    // برگردون recorder
    onProjectionReady(recorder)
}

private fun stopRecording(
    mediaRecorder: MediaRecorder?,
    mediaProjection: MediaProjection?
){
    try {
        mediaRecorder?.stop()
        mediaRecorder?.release()
        mediaProjection?.stop()
    }catch (e: Exception){
        e.printStackTrace()
    }
}