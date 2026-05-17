package com.mkarshnas6.karenstudio.skiingman.ui.screens

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.skiingman.R
import com.mkarshnas6.karenstudio.skiingman.navigation.Screen
import com.mkarshnas6.karenstudio.skiingman.ui.theme.MainLightColor

@Composable
fun HomeScreen(
    navController: NavController,
    context: Context
) {
    var playerName by remember { mutableStateOf("") }

    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.img_bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White.copy(alpha = 0.7f)),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {

                Text(
                    text = "Skiing Man",
                    fontWeight = FontWeight.W800,
                    fontSize = 30.sp,
                    color = Color.Black
                )

                OutlinedTextField(
                    value = playerName,
                    onValueChange = { playerName = it },
                    textStyle = TextStyle(
                        fontSize = 25.sp,
                        color = Color.Black
                    ),
                    placeholder = {
                        Text(
                            text = "Player Name",
                            fontSize = 20.sp,
                            color = Color.Black
                        )
                    }
                )

                Button(
                    onClick = { navController.navigate(Screen.GameScreen.createRoute(playerName.ifBlank { "Player Name" })) },
                    modifier = Modifier
                        .background(MainLightColor, shape = RoundedCornerShape(1.dp))
                        .clip(RoundedCornerShape(1.dp))
                        .size(165.dp, 70.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "Start Game",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black
                    )
                }

                Button(
                    onClick = { navController.navigate(Screen.RankingScreen.route) },
                    modifier = Modifier
                        .background(MainLightColor, shape = RoundedCornerShape(1.dp))
                        .clip(RoundedCornerShape(1.dp))
                        .size(165.dp, 70.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "Rankings",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                }

                Button(
                    onClick = { navController.navigate(Screen.GameScreen.route) },
                    modifier = Modifier
                        .background(MainLightColor, shape = RoundedCornerShape(1.dp))
                        .clip(RoundedCornerShape(1.dp))
                        .size(165.dp, 70.dp),
                    colors = ButtonDefaults.buttonColors(
                        contentColor = Color.Black,
                        containerColor = Color.Transparent
                    )
                ) {
                    Text(
                        text = "Setting",
                        fontSize = 21.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                    )
                }

            }
        }

    }
}