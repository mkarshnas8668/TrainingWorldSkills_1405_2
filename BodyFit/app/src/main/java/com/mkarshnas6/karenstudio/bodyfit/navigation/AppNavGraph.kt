package com.mkarshnas6.karenstudio.bodyfit.navigation

import android.widget.Space
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.modifier.modifierLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mkarshnas6.karenstudio.bodyfit.R
import com.mkarshnas6.karenstudio.bodyfit.ui.screen.home.HomeScreen
import com.mkarshnas6.karenstudio.bodyfit.ui.theme.MainYellow500
import com.mkarshnas6.karenstudio.bodyfit.utils.AppConstant
import com.mkarshnas6.karenstudio.bodyfit.utils.DataStoreManger
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

data class SlideData(
    val title: String,
    val description: String,
    val imageRes: Int
)

@Composable
fun AppNavGraph(
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    val dataStoreManager = remember { DataStoreManger(context) }

    val isFirstLaunch by dataStoreManager
        .readBooleanFlow(AppConstant.DataStore.FIRST_LAUNCH, true)
        .collectAsState(null)

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route,
        modifier = Modifier.padding(innerPadding)
    ) {

        composable(Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                context = context
            )
        }

    }

    when (isFirstLaunch) {
        null -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(modifier = Modifier.size(30.dp), color = Color.Black)
            }
        }

        true -> {

            var currentIndex by remember { mutableIntStateOf(0) }

            var slides = listOf(
                SlideData(
                    "The best sport tracker apps",
                    "Lorem ipsum dolor sti amet, consecture adipiscing elit.",
                    R.drawable.img_intro_1
                ),
                SlideData(
                    "Get more experience with community",
                    "Lorem ipsum dolor sti amet, consecture adipiscing elit.",
                    R.drawable.img_intro_2
                ),
                SlideData(
                    "Connect with your wearable devices",
                    "Lorem ipsum dolor sti amet, consecture adipiscing elit.",
                    R.drawable.img_intro_3
                )
            )

            var endedSlides = currentIndex == slides.size - 1

            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                slides.forEachIndexed { index, info ->

                    Image(
                        painter = painterResource(slides[currentIndex].imageRes),
                        contentDescription = "image intro",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                brush = Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Black.copy(alpha = 0.7f),
                                        Color.Transparent,
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    Column(
                        modifier = Modifier
                            .padding(23.dp)
                            .fillMaxWidth()
                            .align(Alignment.BottomCenter)
                    ) {

                        Text(
                            text = slides[currentIndex].title,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(11.dp))

                        Text(
                            text = slides[currentIndex].description,
                            fontSize = 17.sp,
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(50.dp))

                        Row(
                            modifier = Modifier
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        )
                        {
                            Row {
                                slides.forEachIndexed { index, strings ->
                                    Box(
                                        modifier = Modifier
                                            .padding(horizontal = 4.dp)
                                            .clip(CircleShape)
                                            .size(16.dp)
                                            .background(
                                                if (index == currentIndex) MainYellow500 else Color.White,
                                                shape = CircleShape
                                            )
                                    )
                                }
                            }
                            Button(
                                onClick = {
                                    if (endedSlides) {
                                        CoroutineScope(Dispatchers.IO).launch {
                                            dataStoreManager.saveBoolean(
                                                AppConstant.DataStore.FIRST_LAUNCH,
                                                false
                                            )
                                        }
                                    } else {
                                        currentIndex++
                                    }
                                },
                                colors = ButtonDefaults.buttonColors(
                                    contentColor = Color.Black,
                                    containerColor = MainYellow500
                                )
                            ) {
                                Text(
                                    text = if (!endedSlides) "Next" else "Get Started",
                                    fontSize = 22.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
            }
        }

        false -> {}
    }

}