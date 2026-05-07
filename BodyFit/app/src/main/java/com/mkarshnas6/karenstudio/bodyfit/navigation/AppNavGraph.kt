package com.mkarshnas6.karenstudio.bodyfit.navigation

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.mkarshnas6.karenstudio.bodyfit.ui.screen.home.HomeScreen

@Composable
fun AppNavGraph(
    innerPadding: PaddingValues
) {
    val navController = rememberNavController()
    val context = LocalContext.current

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

}