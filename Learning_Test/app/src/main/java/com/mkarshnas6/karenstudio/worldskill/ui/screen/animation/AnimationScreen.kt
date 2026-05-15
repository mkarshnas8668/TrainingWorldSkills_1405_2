package com.mkarshnas6.karenstudio.worldskill.ui.screen.animation

import android.content.Context
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.EaseInOutQuart
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameNanos
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnimationScreen(
    navController: NavController, context: Context
) {
    var fanSpeed by remember { mutableStateOf(2.5f) }

    val rotation = remember { Animatable(0f) }
    val scope = rememberCoroutineScope()

    LaunchedEffect(fanSpeed) {
        scope.launch {
            while (true) {
                val durationPerRotation = (1000f / fanSpeed).toInt()

                rotation.animateTo(
                    targetValue = 360f,
                    animationSpec = tween(
                        durationMillis = durationPerRotation,
                        easing = LinearEasing
                    )

                )
                rotation.snapTo(0f)
            }
        }

    }


    Column(
        modifier = Modifier
            .padding(20.dp)
            .background(Color.White)
            .verticalScroll(rememberScrollState())
            .fillMaxSize(),
    ) {
        // ball move and back and change color
        var moveBall by remember { mutableStateOf(false) }

        val infiniteTransition = rememberInfiniteTransition()

        val changeColorGradientBackground by infiniteTransition.animateFloat(
            initialValue = 0f,
            targetValue = 1f,
            animationSpec = infiniteRepeatable(
                animation = tween(3000, easing = LinearEasing),
                repeatMode = RepeatMode.Reverse
            )
        )

        Column(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(
                        colors = listOf(
                            Color.Yellow.copy(0.6f),
                            Color.LightGray
                        ),
                        startX = changeColorGradientBackground,
                        endX = changeColorGradientBackground + 1f
                    ),
                    shape = CircleShape
                )
                .fillMaxWidth(),
        ) {
            val animationBallX by animateFloatAsState(
                targetValue = if (moveBall) 310f else 0f,
                animationSpec = infiniteRepeatable(
                    tween(2000, easing = EaseInOutQuart),
                    repeatMode = RepeatMode.Reverse
                ),
            )

            val animationColor_1_Ball by animateColorAsState(
                targetValue = if (moveBall) Color.Red else Color.Blue,
                animationSpec = infiniteRepeatable(
                    tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            val animationColor_2_Ball by animateColorAsState(
                targetValue = if (moveBall) Color.Blue else Color.Red,
                animationSpec = infiniteRepeatable(
                    tween(2000, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            Box(
                modifier = Modifier
                    .size(80.dp)
                    .offset(animationBallX.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                animationColor_1_Ball,
                                animationColor_2_Ball
                            )
                        ),
                        shape = CircleShape
                    )
                    .clip(CircleShape)
                    .clickable { moveBall = !moveBall }
            )
        }

        // fan
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Image(
                painter = painterResource(R.drawable.ic_fan),
                contentDescription = null,
                modifier = Modifier
                    .rotate(rotation.value)
                    .graphicsLayer { scaleX = -1f }
            )
            Slider(
                value = fanSpeed,
                onValueChange = { fanSpeed = it },
                valueRange = 0.1f..5f,
                colors = SliderDefaults.colors(
                    activeTrackColor = Color.Red,
                    activeTickColor = Color.Green
                ),
                thumb = {
                    Box(
                        modifier = Modifier
                            .background(Color.Green)
                            .height(28.dp)
                            .width(10.dp)
                    )
                },
                track = { thumbPosition ->
                    Box(
                        modifier = Modifier
                            .background(Color.Gray)
                            .fillMaxWidth()
                            .height(20.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth((thumbPosition.value * 0.2f))
                                .height(20.dp)
                                .background(
                                    Brush.horizontalGradient(
                                        listOf(Color.Red, Color.Blue)
                                    )
                                )
                        )

                    }
                }
            )

        }

        // scale heart
        Column(
            modifier = Modifier
                .padding(top = 30.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            var heartTap by remember { mutableStateOf(false) }
            val scaleBall by animateFloatAsState(
                targetValue = if (heartTap) 8f else 2f,
                animationSpec = infiniteRepeatable(
                    tween(1000, delayMillis = 200, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )

            LaunchedEffect(Unit) { heartTap = !heartTap }

            Icon(
                imageVector = Icons.Default.Favorite,
                contentDescription = null,
                tint = Color.Red,
                modifier = Modifier.scale(scaleBall)
            )

        }

        // flip card
        var flipped by remember { mutableStateOf(false) }
        val animationRotationY by animateFloatAsState(
            targetValue = if (flipped) 180f else 0f,
            animationSpec = tween(1000, easing = LinearEasing)
        )

        var animationTextCard by remember { mutableStateOf("on Cart") }

        LaunchedEffect(flipped) {
            if (flipped) {
                delay(500)
                animationTextCard = "behind Cart"
            } else {
                delay(500)
                animationTextCard = "on Cart"
            }
        }

        Box(
            modifier = Modifier
                .padding(top = 50.dp)
                .fillMaxWidth()
                .height(200.dp),
            contentAlignment = Alignment.Center
        ) {
            Box(
                modifier = Modifier
                    .size(height = 240.dp, width = 160.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .graphicsLayer {
                        rotationY = animationRotationY
                        cameraDistance = 100f // more deep and bather rotation
                    }
                    .background(Color.Blue, shape = RoundedCornerShape(14.dp))
                    .clickable { flipped = !flipped },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = animationTextCard,
                    fontSize = 22.sp,
                    color = Color.White,
                    modifier = Modifier
                        .graphicsLayer {
                            rotationY = animationRotationY
                        }

                )
            }
        }

        // 3D button when click
        var pressed by remember { mutableStateOf(false) }

        val buttonRotationX by animateFloatAsState(
            targetValue = if (pressed) 45f else 0f,
            animationSpec = tween(200)
        )

        Box(
            modifier = Modifier
                .size(150.dp)
                .graphicsLayer {
                    rotationX = buttonRotationX
                    cameraDistance = 10f
                }
                .clip(RoundedCornerShape(16.dp))
                .background(Color.Red, RoundedCornerShape(16.dp))
                .clickable { pressed = !pressed },
            contentAlignment = Alignment.Center
        ) {
            Text("Click me !!", color = Color.White, fontSize = 24.sp)
        }

        var chartRotationY by remember { mutableStateOf(0f) }

        LaunchedEffect(Unit) {
            while (true) {
                chartRotationY += 0.5f
                withFrameNanos { }
            }
        }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    rotationY = chartRotationY
                    cameraDistance = 20f * density
                },
            contentAlignment = Alignment.Center
        ) {
            Canvas(modifier = Modifier.size(300.dp)) {
                val barWidth = 40f
                val heights = listOf(200f, 350f, 500f, 280f, 420f)

                heights.forEachIndexed { index, height ->
                    drawRect(
                        color = Color.Blue,
                        topLeft = Offset(index * (barWidth + 20f) + 20f, 600f - height),
                        size = Size(barWidth, height)
                    )
                }
            }
        }

    }

}
