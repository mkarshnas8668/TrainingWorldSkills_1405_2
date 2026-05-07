package com.mkarshnas6.karenstudio.bodyfit.navigation

sealed class Screen(val route: String) {

    object HomeScreen : Screen("HomeScreen")

}