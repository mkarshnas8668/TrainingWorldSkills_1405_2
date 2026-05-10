package com.mkarshnas6.karenstudio.worldskill.ui.screen.sensor

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.LifecycleResumeEffect
import androidx.navigation.NavController
import java.util.Locale

@Composable
fun SensorScreen(
    navController: NavController,
    context: Context
) {

    var accelerometerData by remember { mutableStateOf("شتاب سنج : \nX: 0.00\nY: 0.00\nZ: 0.00") }
    var gyroscopeData by remember { mutableStateOf("\n\nژیروسکوپ : \nX: 0.00\nY: 0.00\nZ: 0.00") }

    val sensorManager = context.getSystemService(Context.SENSOR_SERVICE) as SensorManager

    LifecycleResumeEffect(Unit) {
        val sensorListener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent?) {
                event ?: return
                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        accelerometerData = String.format(
                            Locale.US,
                            "شتاب سنج :\nX: %.2f\nY: %.2f\nZ: %.2f",
                            x, y, z
                        )
                    }

                    Sensor.TYPE_GYROSCOPE -> {
                        val x = event.values[0]
                        val y = event.values[1]
                        val z = event.values[2]
                        gyroscopeData = String.format(
                            Locale.US,
                            "\n\nژیروسکوپ :\nX: %.2f\nY: %.2f\nZ: %.2f",
                            x, y, z
                        )
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}
        }

        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE)

        accelerometer?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }
        gyroscope?.let {
            sensorManager.registerListener(sensorListener, it, SensorManager.SENSOR_DELAY_NORMAL)
        }

        onPauseOrDispose { sensorManager.unregisterListener(sensorListener) }
    }

// simple UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "$accelerometerData$gyroscopeData",
                color = Color.Green,
                fontSize = 22.sp,
                fontFamily = FontFamily.Monospace
            )
        }
    }

}