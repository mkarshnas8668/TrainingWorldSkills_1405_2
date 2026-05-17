package com.mkarshnas6.karenstudio.skiingman

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mkarshnas6.karenstudio.skiingman.navigation.AppNavGraph
import com.mkarshnas6.karenstudio.skiingman.ui.theme.SkiingManTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SkiingManTheme {
                AppNavGraph()
            }
        }
    }
}
