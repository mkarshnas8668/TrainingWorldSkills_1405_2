package com.mkarshnas6.karenstudio.worldskill.ui.screen.contentProvider

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun ContactScreen(
    navController: NavController,
    context: Context
) {

    var contacts by remember { mutableStateOf(listOf<Pair<String, String>>()) }

    val activity = context as ComponentActivity

    var isGranted by remember {
        mutableStateOf(
            ContextCompat.checkSelfPermission(
                activity,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        )
    }

    val showRationale by remember {
        mutableStateOf(
            ActivityCompat.shouldShowRequestPermissionRationale(
                activity,
                Manifest.permission.READ_CONTACTS
            )
        )
    }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        isGranted = granted
        if (granted) {
            Toast.makeText(context, "we have permission", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "we not have permission", Toast.LENGTH_SHORT).show()
        }
    }

    Column(modifier = Modifier.fillMaxSize()) {
        if (showRationale) {
            Text("we need permission read contacts please accept !!")
        }
        Button(
            onClick = {
                if (!isGranted) {
                    launcher.launch(Manifest.permission.POST_NOTIFICATIONS)
                } else {
                    Toast.makeText(context, "Already have permission", Toast.LENGTH_SHORT).show()
                }
            }
        ) { Text("read contact permission") }
    }

}