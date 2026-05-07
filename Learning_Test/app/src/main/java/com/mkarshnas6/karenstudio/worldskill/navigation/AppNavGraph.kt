package com.mkarshnas6.karenstudio.worldskill.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.mkarshnas6.karenstudio.worldskill.ui.screen.dataNav.DataNavScreen
import com.mkarshnas6.karenstudio.worldskill.ui.screen.home.HomeScreen
import com.mkarshnas6.karenstudio.worldskill.utils.SharedPrefsManager

@Composable
fun AppNavGraph(
    prefsManager: SharedPrefsManager
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

    }

}