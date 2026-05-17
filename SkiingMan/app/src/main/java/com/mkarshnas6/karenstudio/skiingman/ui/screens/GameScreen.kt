package com.mkarshnas6.karenstudio.skiingman.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.media.MediaPlayer
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.skiingman.R
import kotlinx.coroutines.delay
import kotlin.random.Random

data class Obstacle(
    val id: Int,
    val xOffset: Float,
    val yOffset: Float,
    val width: Float,
    val height: Float
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun GameScreen(
    navController: NavController,
    context: Context,
    playerName: String
) {
    val configuration = LocalConfiguration.current
    var screenWidth by remember { mutableStateOf(configuration.screenWidthDp) }
    var screenHeight by remember { mutableStateOf(configuration.screenHeightDp) }

    var isGameOver by remember { mutableStateOf(false) }

    LaunchedEffect(configuration) {
        screenWidth = configuration.screenWidthDp
        screenHeight = configuration.screenHeightDp
    }

    var list_obstacle by remember { mutableStateOf<List<Obstacle>>(emptyList()) }

    var nextAbstractId by remember { mutableIntStateOf(0) }
    var counterScore by remember { mutableIntStateOf(0) }
    var counterCoin by remember { mutableIntStateOf(0) }

    var isRotateGround by remember { mutableStateOf(false) }

    val animationRotateGround by animateFloatAsState(
        if (isRotateGround) 10f else 0f,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    val animationOffsetXGround by animateDpAsState(
        if (isRotateGround) -10.dp else 0.dp,
        animationSpec = tween(1000, easing = LinearEasing)
    )

    var startChalleng by remember { mutableStateOf(false) }

    var skiingManIsJumping by remember { mutableStateOf(false) }

    val animationJumpingSkiingMan by animateDpAsState(
        targetValue = if (skiingManIsJumping) (-100).dp else 40.dp,
        animationSpec = tween(300, easing = LinearEasing),
        finishedListener = { skiingManIsJumping = false }
    )

    // controll music game .................
    var mediaPlayerBackgrond by remember { mutableStateOf<MediaPlayer?>(null) }
    var mediaPlayerJump by remember { mutableStateOf<MediaPlayer?>(null) }
    var mediaPlayerGameOver by remember { mutableStateOf<MediaPlayer?>(null) }
    DisposableEffect(Unit) {
        onDispose {
            mediaPlayerBackgrond?.release()
            mediaPlayerBackgrond = null
        }
    }

    LaunchedEffect(skiingManIsJumping, isGameOver) {
        if (skiingManIsJumping)
            mediaPlayerJump?.start()
        if (isGameOver)
            mediaPlayerGameOver?.start()
    }

    // start game
    LaunchedEffect(
        Unit
    ) {
        if (mediaPlayerBackgrond == null) {
            mediaPlayerBackgrond = MediaPlayer.create(context, R.raw.audio_play_background)
            mediaPlayerBackgrond?.setOnCompletionListener {
                mediaPlayerBackgrond?.start()
            }
            mediaPlayerBackgrond?.start()
        }
        if (mediaPlayerJump == null) {
            mediaPlayerJump = MediaPlayer.create(context, R.raw.audio_jump)
        }
        if (mediaPlayerGameOver == null) {
            mediaPlayerGameOver = MediaPlayer.create(context, R.raw.audio_game_over)
        }
        delay(4500)
        isRotateGround = true
        startChalleng = true
    }

    LaunchedEffect(startChalleng) {
        if (startChalleng) {
            var lastGenerationTime = System.currentTimeMillis()
            while (true) {
                var currentTime = System.currentTimeMillis()
                if ((currentTime - lastGenerationTime) >= Random.nextInt(1300, 3000)) {
                    val newObstacle = Obstacle(
                        id = nextAbstractId,
                        xOffset = (screenWidth + 40).toFloat(),
                        yOffset = 40f,
                        width = 40f,
                        height = 40f
                    )
                    list_obstacle = list_obstacle + newObstacle
                    lastGenerationTime = currentTime
                    nextAbstractId++
                }

                val skiingManx = (screenWidth / 2) - 30f
                val skiingMany = (screenHeight - 150f)
                val skiingManWidth = 60f
                val skiingManHeight = 100f

                for (obstacle in list_obstacle) {
                    if (
                        checkCollision(
                            skiingManx, skiingMany,
                            skiingManWidth, skiingManHeight,
                            obstacle
                        )
                    ) {
                        isGameOver = true
                        startChalleng = false
                        mediaPlayerBackgrond?.stop()
                        break
                    }
                }

                if (isGameOver) break else counterScore++

                // move all obstacles
                list_obstacle = list_obstacle.map { obstacle ->
                    obstacle.copy(xOffset = obstacle.xOffset - 7f)
                }.filter { it.xOffset > -it.width - screenWidth }

                withFrameNanos { }
            }
        }
    }

    if (isGameOver) {
        Dialog(
            onDismissRequest = { isGameOver = false },
            properties = DialogProperties(
                dismissOnBackPress = false,
                dismissOnClickOutside = false
            ),
        ) {
            Box(
                modifier = Modifier
                    .background(Color.White)
                    .padding(20.dp)
            ) {
                Column {
                    Text("Game Over", color = Color.Red, fontSize = 24.sp)
                    Button(onClick = { isGameOver = true }) { Text("OK") }
                }
            }
        }
    }

    // ............... sensors ...................
    val sensorManager = context.getSystemService(SensorManager::class.java)

    var gyroscopeDataY by remember { mutableStateOf(0f) }

    // set sensors
    LifecycleResumeEffect(Unit) {
        val sensorListener = object : SensorEventListener {
            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                when (event.sensor.type) {
                    Sensor.TYPE_GYROSCOPE -> {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        gyroscopeDataY = y

                        if (y >= 0.1 && !skiingManIsJumping) {
                            skiingManIsJumping = true
                        }
                    }
                }
            }
        }

        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        gyroscope?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        onPauseOrDispose {
            sensorManager.unregisterListener(sensorListener)
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center,
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .clickable { if (!skiingManIsJumping) skiingManIsJumping = true })

        Image(
            painter = painterResource(R.drawable.img_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Image(
            painter = painterResource(R.drawable.ic_trees),
            contentDescription = null,
            modifier = Modifier
                .height(500.dp)
                .align(Alignment.BottomStart)
                .offset(y = -30.dp)
                .fillMaxWidth(),
            contentScale = ContentScale.FillHeight
        )

        // show sensor y gyroscope
        Row(
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = gyroscopeDataY.toString(),
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Bottom,
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {

            Box(contentAlignment = Alignment.BottomCenter) {
                // obstacles
                list_obstacle.forEach { obstacle ->
                    Image(
                        painter = painterResource(R.drawable.ic_obstacle),
                        contentDescription = null,
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .rotate(animationRotateGround)
                            .offset(x = obstacle.xOffset.dp, y = -75.dp)
                            .width(obstacle.width.dp)
                            .height(obstacle.height.dp),
                        contentScale = ContentScale.Crop
                    )
                }
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Bottom,
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    // char
                    Image(
                        painter = painterResource(R.drawable.ic_skiing_person),
                        contentDescription = null,
                        modifier = Modifier
                            .height(100.dp)
                            .offset(y = animationJumpingSkiingMan)
                            .rotate(animationRotateGround)
                            .background(Color.Transparent)
                            .clickable {
                                if (!skiingManIsJumping)
                                    skiingManIsJumping = true

                            },
                        contentScale = ContentScale.FillHeight
                    )

                    // ground
                    Box(
                        modifier = Modifier
                            .offset(x = animationOffsetXGround, y = 36.dp)
                            .rotate(animationRotateGround)
                            .background(Color.White)
                            .width(1800.dp)
                            .height(120.dp)
                    )
                }
            }

        }
    }
}

fun checkCollision(
    skiingManX: Float,
    skiingManY: Float,
    skiingManWidth: Float,
    skiingManHeight: Float,
    obstacle: Obstacle
): Boolean {
    println("===== COLLISION CHECK =====")
    println("SkiingMan: x=$skiingManX, y=$skiingManY, w=$skiingManWidth, h=$skiingManHeight")
    println("Obstacle: x=${obstacle.xOffset}, y=${obstacle.yOffset}, w=${obstacle.width}, h=${obstacle.height}")

    val isColliding = skiingManX < obstacle.xOffset + obstacle.width &&
            skiingManX + skiingManWidth > obstacle.xOffset &&
            skiingManY < obstacle.yOffset + obstacle.height &&
            skiingManY + skiingManHeight > obstacle.yOffset

    println("Collision: $isColliding")
    return isColliding
}