package com.mkarshnas6.karenstudio.worldskill.navigation

sealed class Screen(val route: String) {

    object HomeScreen : Screen("HomeScreen")

    object DataNavScreen : Screen("DataNavScreen/{pageNumber}") {
        fun createRoute(pageNumber: Int): String = "DataNavScreen/$pageNumber"
    }

}