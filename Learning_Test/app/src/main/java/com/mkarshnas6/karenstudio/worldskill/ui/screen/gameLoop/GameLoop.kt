package com.mkarshnas6.karenstudio.worldskill.ui.screen.gameLoop

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun GameLoop(
    navController: NavController,
    context: Context
) {
    var widthScreen by remember { mutableStateOf(200) }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .onSizeChanged {
                widthScreen = it.width
            })
    // variables for changing and draw animation
    var ballX by remember { mutableStateOf(100f) }
    var bally by remember { mutableStateOf(100f) }
    var ballSpeedX by remember { mutableStateOf(5f) }
    var ballSpeedY by remember { mutableStateOf(5f) }

    //1 _ this is game loop
    LaunchedEffect(Unit) {
        while (true) {
            // update move
            ballX += ballSpeedX
            bally += ballSpeedY

            // in the wall
            if (ballX <= 50f || ballX >= 950f) ballSpeedX = -ballSpeedX
            if (bally <= 50f || bally >= 950f) ballSpeedY = -ballSpeedY

            // wait till next frame
            withFrameNanos { } // this not stop just connect to screen
        }
    }

//    2 _ with animation
    val infiniteTransition = rememberInfiniteTransition()
    val x by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = (widthScreen).toFloat(),
        animationSpec = infiniteRepeatable(
            animation = tween(2000, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

//    3 _ path with animation
    var startAnimationScaleBall by remember { mutableStateOf(false) }
    val scaleCircle by animateFloatAsState(
        targetValue = if (startAnimationScaleBall) 150f else 10f,
        animationSpec = infiniteRepeatable(
            tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        )
    )

    val colorCircle by animateColorAsState(
        if (startAnimationScaleBall) Color.Blue else Color.Yellow,
        animationSpec = infiniteRepeatable(tween(2000), repeatMode = RepeatMode.Reverse)
    )

    LaunchedEffect(Unit) {
        startAnimationScaleBall = true
    }

    Canvas(modifier = Modifier.fillMaxSize()) {
        drawCircle(
            color = Color.Red,
            radius = 100f,
            center = Offset(ballX, bally)
        )

        drawCircle(Color.Green, 100f, Offset(x, 1000f))

        drawCircle(
            brush = Brush.linearGradient(
                colors = listOf(Color.Green, colorCircle, Color.Red),
                start = Offset(0f, 1200f - scaleCircle),
                end = Offset(0f, 1200f + scaleCircle),
            ),
            radius = scaleCircle,
            center = Offset(300f, 1200f)
        )
    }

    Spacer(modifier = Modifier.height(100.dp))
    RotatingCube()

}

@Composable
fun RotatingCube() {
    var angleX by remember { mutableStateOf(0f) }
    var angleY by remember { mutableStateOf(0f) }

    LaunchedEffect(Unit) {
        while (true) {
            withFrameNanos {
                angleX += 0.01f
                angleY += 0.015f
            }
        }
    }

    Canvas(modifier = Modifier.fillMaxWidth().height(100.dp).background(Color.Black)) {
        val centerX = size.width / 2
        val centerY = size.height / 2
        val size = 200f

        // ۸ نقطه مکعب
        val points = arrayOf(
            Triple(-1f, -1f, -1f), Triple(1f, -1f, -1f),
            Triple(1f, 1f, -1f), Triple(-1f, 1f, -1f),
            Triple(-1f, -1f, 1f), Triple(1f, -1f, 1f),
            Triple(1f, 1f, 1f), Triple(-1f, 1f, 1f)
        )

        // چرخش و تبدیل به ۲بعدی
        val projected = points.map { (x, y, z) ->
            // چرخش حول X
            var ry = y * cos(angleX) - z * sin(angleX)
            var rz = y * sin(angleX) + z * cos(angleX)

            // چرخش حول Y
            var rx = x * cos(angleY) + rz * sin(angleY)
            rz = -x * sin(angleY) + rz * cos(angleY)

            // تبدیل پرسپکتیو
            val scale = 2f / (4f + rz)
            Offset(
                centerX + rx * size * scale,
                centerY + ry * size * scale
            )
        }

        // رسم یال‌های مکعب
        val edges = listOf(0 to 1, 1 to 2, 2 to 3, 3 to 0,
            4 to 5, 5 to 6, 6 to 7, 7 to 4,
            0 to 4, 1 to 5, 2 to 6, 3 to 7)

        edges.forEach { (a, b) ->
            drawLine(Color.Cyan, projected[a], projected[b], strokeWidth = 3f)
        }

        // رسم نقاط
        projected.forEach { point ->
            drawCircle(Color.White, 6f, point)
        }
    }
}
