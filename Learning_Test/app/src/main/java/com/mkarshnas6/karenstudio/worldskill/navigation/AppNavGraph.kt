package com.mkarshnas6.karenstudio.worldskill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mkarshnas6.karenstudio.worldskill.ui.screen.biometric.BiometricScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.camera.CameraScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.chat.ChatScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.clipboard.ClipboardScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.contentProvider.ContactScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.dataNav.DataNavScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.dragDrop.DragDropScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.dynamicBroadcast.DynamicBroadcastScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.encryption.EncryptionScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.fileProvider.FileProviderSimpleScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.foregroundService.ForegroundServiceScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.geofence.GeofenceScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.home.HomeScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.location.LocationScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.mediaPlayer.MusicPlayerScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.mediaPlayer.VideoPlayerScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.notification.NotificationScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.onlineShop.OnlineShopScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.screenRecroder.ScreenRecorderScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.sensor.SensorScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.shop.ShopScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.sse.SSEScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.telephony.TelephonyScreen
import com.mkarshnas6.karenstudio.worldskill.utils.SharedPrefsManager

@Composable
fun AppNavGraph(
    prefsManager: SharedPrefsManager,
    onRegister: () -> Unit,
    onUnregister: () -> Unit,
    batteryStatus: String = "---",
    networkStatus: String = "---",
) {
    val navController = rememberNavController()
    val context = LocalContext.current

    NavHost(
        navController = navController,
        startDestination = Screen.HomeScreen.route
    ) {

        composable(route = Screen.HomeScreen.route) {
            HomeScreen(
                navController = navController,
                context = context,
            )
        }

        composable(
            route = Screen.DataNavScreen.route,
            arguments = listOf(
                navArgument("pageNumber") {
                    type = NavType.IntType
                }
            )
        ) { backStackEntry ->
            val pageNumber = backStackEntry.arguments?.getInt("pageNumber") ?: 0

            DataNavScreen(
                navController = navController,
                context = context,
                pageNumber = pageNumber
            )
        }

        composable(Screen.FileProviderScreen.route) {
            FileProviderSimpleScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.EncryptionScreen.route) {
            EncryptionScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.ShopScreen.route) {
            ShopScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.OnlineShopScreen.route) {
            OnlineShopScreen(
                navController = navController,
                context = context
            )
        }

        composable(
            route = Screen.ChatScreenWS.route,
            arguments = listOf(
                navArgument("userId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val userId = backStackEntry.arguments?.getString("userId") ?: ""
            ChatScreen(userId)
        }

        composable(Screen.SSEScreen.route) {
            SSEScreen()
        }

        composable(Screen.SensorScreen.route) {
            SensorScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.CameraScreen.route) {
            CameraScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.BiometricScreen.route) {
            BiometricScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.ClipboardScreen.route) {
            ClipboardScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.DragDropScreen.route) {
            DragDropScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.ForegroundServiceScreen.route) {
            ForegroundServiceScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.LocationScreen.route) {
            LocationScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.GeofenceScreen.route) {
            GeofenceScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.DynamicBroadcast.route) {
            DynamicBroadcastScreen(
                onRegister = onRegister,
                onUnregister = onUnregister,
                navController = navController,
                context = context,
                batteryStatus = batteryStatus,
                networkStatus = networkStatus
            )
        }

        composable(Screen.NotificationScreen.route) {
            NotificationScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.MusicPlayerScreen.route) {
            MusicPlayerScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.VideoPlayerScreen.route) {
            VideoPlayerScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.ScreenRecorderScreen.route) {
            ScreenRecorderScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.ContactScreen.route) {
            ContactScreen(
                navController = navController,
                context = context
            )
        }

        composable(Screen.TelephonyScreen.route) {
            TelephonyScreen(
                navController = navController,
                context = context
            )
        }

    }

}