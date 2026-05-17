package com.mkarshnas6.karenstudio.skiingman.navigation

sealed class Screen(val route: String) {
    object HomeScreen : Screen("HomeScreen")

    object GameScreen : Screen("GameScreen/{playerName}") {
        fun createRoute(playerName: String): String = "GameScreen/$playerName"
    }

    object RankingScreen : Screen("RankingScreen")
    object SettingScreen : Screen("SettingScreen")
}