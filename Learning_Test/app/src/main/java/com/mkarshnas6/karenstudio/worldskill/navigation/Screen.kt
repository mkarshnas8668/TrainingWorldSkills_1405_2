package com.mkarshnas6.karenstudio.worldskill.navigation

sealed class Screen(val route: String) {

    object HomeScreen : Screen("HomeScreen")

    object DataNavScreen : Screen("DataNavScreen/{pageNumber}") {
        fun createRoute(pageNumber: Int): String = "DataNavScreen/$pageNumber"
    }

    object FileProviderScreen : Screen("FileProviderScreen")

    object EncryptionScreen : Screen("EncryptionScreen")

    object ShopScreen : Screen("ShopScreen")

    object OnlineShopScreen : Screen("OnlineShopScreen")

    object ChatScreenWS : Screen("ChatScreenWS/{userId}") {
        fun createRoute(userId: String): String = "ChatScreenWS/$userId"
    }

    object SSEScreen : Screen("SSEScreen")

    object SensorScreen : Screen("SensorScreen")

    object CameraScreen : Screen("CameraScreen")

    object Biometric : Screen("BiometricScreen")

}