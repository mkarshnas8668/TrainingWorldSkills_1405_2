package com.mkarshnas6.karenstudio.worldskill

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.mkarshnas6.karenstudio.worldskill.data.remote.RetrofitClient
import com.mkarshnas6.karenstudio.worldskill.navigation.AppNavGraph
import com.mkarshnas6.karenstudio.worldskill.ui.theme.WorldSkillTheme
import com.mkarshnas6.karenstudio.worldskill.utils.SharedPrefsManager

class MainActivity : ComponentActivity() {
    private lateinit var prefsManager: SharedPrefsManager
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        RetrofitClient.init(this)

        prefsManager = SharedPrefsManager(this)
        setContent {
            WorldSkillTheme {
                AppNavGraph(
                    prefsManager = prefsManager
                )
            }
        }
    }
}
