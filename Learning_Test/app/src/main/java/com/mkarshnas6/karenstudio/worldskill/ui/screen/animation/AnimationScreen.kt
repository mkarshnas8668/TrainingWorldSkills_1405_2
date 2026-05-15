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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.R
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

    }

}