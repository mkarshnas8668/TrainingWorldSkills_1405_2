package com.mkarshnas6.karenstudio.myfrance.navigation

import android.annotation.SuppressLint
import androidx.annotation.DrawableRes
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricPrompt
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.fragment.app.FragmentActivity
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mkarshnas6.karenstudio.myfrance.R
import com.mkarshnas6.karenstudio.myfrance.ui.screens.HomeScreen
import com.mkarshnas6.karenstudio.myfrance.ui.screens.ProfileScreen
import com.mkarshnas6.karenstudio.myfrance.ui.screens.TravelScreen

data class ItemTabNav(
    val name: String,
    val navigateScreen: String,
    @DrawableRes val icon: Int
)

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun AppNavGraph() {

    val navController = rememberNavController()
    val context = LocalContext.current

    val configuration = LocalConfiguration.current
    val widthScreen by remember { mutableStateOf(configuration.screenWidthDp) }
    val heightScreen by remember { mutableStateOf(configuration.screenHeightDp) }

    var tabSelection by remember { mutableStateOf(0) }

    val listTabs by remember {
        mutableStateOf(
            listOf(
                ItemTabNav(
                    "Home",
                    Screen.HomeScreen.route,
                    R.drawable.icon_home_outline,
                ),
                ItemTabNav(
                    "Travel",
                    Screen.TravelScreen.route,
                    R.drawable.icon_map_outline
                ),
                ItemTabNav(
                    "Account",
                    Screen.ProfileScreen.route,
                    R.drawable.icon_account_circle_outline
                ),
            )
        )
    }

    var isAuthenticated by remember { mutableStateOf(false) }

    var statusAuthentication by remember { mutableStateOf("put your finger .") }

    val biometricManager = remember { BiometricManager.from(context) }

    val canAuthenticate = remember {
        when (biometricManager.canAuthenticate(BiometricManager.Authenticators.BIOMETRIC_STRONG)) {
            BiometricManager.BIOMETRIC_SUCCESS -> true
            else -> false
        }
    }

    val executor = remember { ContextCompat.getMainExecutor(context) }

    val biometricPrompt: BiometricPrompt = remember {
        BiometricPrompt(
            context as FragmentActivity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    super.onAuthenticationSucceeded(result)
                    isAuthenticated = true
                    statusAuthentication = "your fingring is right"
                }

                override fun onAuthenticationFailed() {
                    super.onAuthenticationFailed()
                    isAuthenticated = false
                    statusAuthentication = "your fingring not right !!"
                }

                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    super.onAuthenticationError(errorCode, errString)
                    isAuthenticated = false
                    statusAuthentication = "error in fingring : $errString"
                }
            }
        )
    }

    val promptInfo = BiometricPrompt.PromptInfo.Builder()
        .setTitle("Authenticator")
        .setSubtitle("put you finger on :)")
        .setDescription("for opnening the app put you finget On :)")
        .setNegativeButtonText("i dont want fingring !!")
        .build()

    LaunchedEffect(Unit) {
        if (canAuthenticate)
            biometricPrompt.authenticate(promptInfo)
    }


    if (isAuthenticated) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .padding(horizontal = 8.dp, vertical = 10.dp)
                    .fillMaxHeight()
                    .background(Color.White)
                    .width((widthScreen / 6).dp)
            ) {

                Spacer(modifier = Modifier.height(30.dp))

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(60.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Spacer(modifier = Modifier.weight(0.1f))
                    Box(
                        modifier = Modifier
                            .weight(0.3f)
                            .shadow(12.dp, shape = CircleShape)
                            .background(Color.White, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.art_icon_la_tour_eiffel),
                            contentDescription = null,
                            modifier = Modifier
                                .clip(CircleShape)
                                .fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Text(
                        text = "My\nFrance",
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(0.7f)
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                listTabs.forEachIndexed { index, nav ->
                    val tabIsSelected =
                        index == tabSelection || (navController.currentBackStackEntry?.destination?.route
                            ?: 0) == index
                    val animChangeColorTabs by animateColorAsState(
                        targetValue = if (tabIsSelected) Color.LightGray.copy(0.5f) else Color.White,
                        animationSpec = tween(300, easing = LinearEasing)
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(91.dp)
                            .background(animChangeColorTabs)
                            .clickable {
                                navController.navigate(nav.navigateScreen)
                                tabSelection = index
                            },
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            painter = painterResource(nav.icon),
                            contentDescription = null,
                            modifier = Modifier.size(39.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = nav.name,
                            fontSize = 22.sp,
                            color = Color.Black,
                        )
                    }
                }

            }

            NavHost(
                navController = navController,
                startDestination = Screen.HomeScreen.route
            ) {
                composable(Screen.HomeScreen.route) {
                    HomeScreen(
                        navController,
                        context
                    )
                }

                composable(Screen.TravelScreen.route) {
                    TravelScreen(
                        navController,
                        context
                    )
                }

                composable(Screen.ProfileScreen.route) {
                    ProfileScreen(
                        navController,
                        context
                    )
                }
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Authentication",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
            HorizontalDivider(modifier = Modifier.background(Color.Red))
            Text(
                text = statusAuthentication,
                fontSize = 20.sp,
                color = Color.Green
            )
        }
    }

}