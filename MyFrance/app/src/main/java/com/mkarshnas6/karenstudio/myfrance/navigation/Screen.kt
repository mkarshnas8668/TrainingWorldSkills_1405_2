package com.mkarshnas6.karenstudio.myfrance.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("HomeScreen")
    object TravelScreen : Screen("TravelScreen")
    object ProfileScreen : Screen("ProfileScreen")
}