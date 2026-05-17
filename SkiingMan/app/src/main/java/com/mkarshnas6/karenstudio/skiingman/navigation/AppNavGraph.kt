package com.mkarshnas6.karenstudio.skiingman.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mkarshnas6.karenstudio.skiingman.ui.screens.GameScreen
import com.mkarshnas6.karenstudio.skiingman.ui.screens.HomeScreen
import com.mkarshnas6.karenstudio.skiingman.ui.screens.RankingScreen
import com.mkarshnas6.karenstudio.skiingman.ui.screens.SettingScreen

@Composable
fun AppNavGraph() {
    val navController = rememberNavController()
    val context = LocalContext.current

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

        composable(
            route = Screen.GameScreen.route,
            arguments = listOf(
                navArgument("playerName") {
                    type = NavType.StringType
                }
            )
        ) { navBackStackEntry ->
            val playerName = navBackStackEntry.arguments?.getString("playerName") ?: "Player Name"

            GameScreen(
                navController,
                context,
                playerName
            )
        }

        composable(Screen.RankingScreen.route) {
            RankingScreen(
                navController, context
            )
        }

        composable(Screen.SettingScreen.route) {
            SettingScreen(
                navController, context
            )
        }

    }

}