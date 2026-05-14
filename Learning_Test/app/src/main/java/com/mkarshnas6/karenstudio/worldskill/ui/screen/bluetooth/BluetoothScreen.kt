import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.Build
import android.os.Handler
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController

@Composable
fun BluetoothScreen(
    navController: NavController,
    context: Context
) {
    val bluetoothManager = remember {
        context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
    }
    val bluetoothAdapter = bluetoothManager.adapter

    var devices by remember { mutableStateOf(listOf<BluetoothDevice>()) }
    var isScanning by remember { mutableStateOf(false) }
    var isEnabled by remember { mutableStateOf(bluetoothAdapter?.isEnabled == true) }

    // ۱. Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val allGranted = permissions.values.all { it }
        if (allGranted) {
            // اجازه‌ها داده شد، اسکن کن
            startScan(bluetoothAdapter, context)
            isScanning = true
            devices = emptyList()
        } else {
            Toast.makeText(context, "اجازه‌ها رد شد", Toast.LENGTH_SHORT).show()
        }
    }

    // ۲. BroadcastReceiver
    val scanReceiver = remember {
        object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                if (BluetoothDevice.ACTION_FOUND == intent?.action) {
                    val device = intent.getParcelableExtra<BluetoothDevice>(
                        BluetoothDevice.EXTRA_DEVICE
                    )
                    if (device != null && !devices.contains(device)) {
                        devices = devices + device
                    }
                }
            }
        }
    }

    // ۳. ثبت Receiver
    DisposableEffect(Unit) {
        val filter = IntentFilter(BluetoothDevice.ACTION_FOUND)
        context.registerReceiver(scanReceiver, filter)
        onDispose {
            try {
                context.unregisterReceiver(scanReceiver)
                bluetoothAdapter?.cancelDiscovery()
            } catch (e: Exception) {
            }
        }
    }

    // ۴. تابع چک کردن همه Permission ها
    fun hasAllPermissions(): Boolean {
        val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            arrayOf(
                Manifest.permission.BLUETOOTH_SCAN,
                Manifest.permission.BLUETOOTH_CONNECT,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        } else {
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
        }

        return permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    // ۵. تابع چک کردن Location
    fun isLocationEnabled(): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "بلوتوث: ${if (isEnabled) "✅ روشن" else "❌ خاموش"}",
            style = MaterialTheme.typography.titleMedium
        )

        // ۶. چک کردن Location
        if (!isLocationEnabled()) {
            Text(
                text = "⚠️ لطفاً Location را روشن کنید",
                color = Color.Red
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            onClick = {
                // ۷. چک کردن Location
                if (!isLocationEnabled()) {
                    Toast.makeText(context, "لطفاً Location را روشن کنید", Toast.LENGTH_SHORT)
                        .show()
                    return@Button
                }

                // ۸. روشن کردن بلوتوث
                if (!isEnabled) {
                    bluetoothAdapter?.enable()
                    isEnabled = true
                    Toast.makeText(context, "بلوتوث روشن شد", Toast.LENGTH_SHORT).show()
                }

                // ۹. چک کردن Permission
                if (hasAllPermissions()) {
                    // اجازه‌ها هست، اسکن کن
                    startScan(bluetoothAdapter, context)
                    isScanning = true
                    devices = emptyList()
                } else {
                    // درخواست اجازه‌ها
                    val permissions = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        arrayOf(
                            Manifest.permission.BLUETOOTH_SCAN,
                            Manifest.permission.BLUETOOTH_CONNECT,
                            Manifest.permission.ACCESS_FINE_LOCATION
                        )
                    } else {
                        arrayOf(Manifest.permission.ACCESS_FINE_LOCATION)
                    }
                    permissionLauncher.launch(permissions)
                }
            },
            colors = ButtonDefaults.buttonColors(
                containerColor = if (isScanning) Color.Red else Color.Blue
            )
        ) {
            Text(if (isScanning) "در حال اسکن..." else "اسکن دستگاه‌ها")
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text("${devices.size} دستگاه پیدا شد")

        LazyColumn {
            items(devices) { device ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(4.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFEEEEEE))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Text("📱 ${device.name ?: "ناشناخته"}")
                        Text("آدرس: ${device.address}")
                    }
                }
            }

            if (devices.isEmpty()) {
                item {
                    Text("دستگاهی پیدا نشد", modifier = Modifier.padding(16.dp))
                }
            }
        }
    }
}

// ۱۰. تابع اسکن (جدا شده)
private fun startScan(bluetoothAdapter: BluetoothAdapter?, context: Context) {
    bluetoothAdapter?.startDiscovery()

    // توقف بعد ۱۰ ثانیه
    Handler(context.mainLooper).postDelayed({
        bluetoothAdapter?.cancelDiscovery()
    }, 10000)
}