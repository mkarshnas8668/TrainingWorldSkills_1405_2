package com.mkarshnas6.karenstudio.worldskill.ui.screen.home

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.navigation.Screen
import com.mkarshnas6.karenstudio.worldskill.viewModel.DataStoreViewModel

@Composable
fun HomeScreen(
    navController: NavController,
    context: Context,
    viewModel: DataStoreViewModel = viewModel()
) {

    val savedName by viewModel.name.collectAsState()
    val savedNotif by viewModel.notif.collectAsState()

    var nameTextField by remember { mutableStateOf(savedName) }
    var notifChecked by remember { mutableStateOf(savedNotif) }

    LaunchedEffect(savedName, savedNotif) {
        nameTextField = savedName
        notifChecked = savedNotif
    }

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Card() {
            Column {
                Text("name : $nameTextField")
                Spacer(modifier = Modifier.height(10.dp))
                Checkbox(
                    checked = notifChecked,
                    onCheckedChange = { notifChecked = it },
                    colors = CheckboxDefaults.colors(
                        checkedColor = Color(0xFF368239)
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        TextField(
            value = nameTextField,
            onValueChange = { nameTextField = it }
        )
        Spacer(modifier = Modifier.height(10.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Button(
            onClick = { viewModel.saveName(nameTextField);viewModel.saveNotif(notifChecked) }
        ) {
            Text("Save")
        }

        Button(
            onClick = { navController.navigate(Screen.FileProviderScreen.route) }
        ) {
            Text("fileProvider")
        }

        Button(
            onClick = { navController.navigate(Screen.EncryptionScreen.route) }
        ) {
            Text("encryption")
        }

        Button(
            onClick = { navController.navigate(Screen.ShopScreen.route) }
        ) {
            Text("Shop")
        }

    }
}