package com.mkarshnas6.karenstudio.worldskill.ui.screen.shop

import android.content.Context
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.mkarshnas6.karenstudio.worldskill.data.local.AppDatabase
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.ProductEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun ShopScreen(
    navController: NavController,
    context: Context
) {

    val dataBase = remember { AppDatabase.getDatabase(context) }

    val productDao = remember { dataBase.productDao() }

    val scope = rememberCoroutineScope()

    var products by remember { mutableStateOf<List<ProductEntity>>(emptyList()) }

    var showDialogState by remember { mutableStateOf(false) }
    var productName by remember { mutableStateOf("") }
    var productPrice by remember { mutableStateOf("") }
    var productStock by remember { mutableStateOf("") }
    var searchProducts by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        productDao.getAllProducts().collect { listProducts ->
            products = listProducts
        }
    }

    // ۸. UI
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = "🏪 مدیریت محصولات",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(modifier = Modifier.height(8.dp))

        Button(
            onClick = { showDialogState = true },
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("+ افزودن محصول")
        }

        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = searchProducts,
            onValueChange = {
                searchProducts = it
                CoroutineScope(Dispatchers.IO).launch {
                    productDao.searchProducts(it)
                        .collect { productEntities ->
                            products = productEntities
                        }
                }
            },
            label = {
                Text(
                    "سرچ",
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Right
                )
            },
            modifier = Modifier.fillMaxWidth(),
            textStyle = TextStyle(
                textAlign = TextAlign.Right,
                fontWeight = FontWeight.Bold,
                color = Color.Black
            )
        )

        Spacer(modifier = Modifier.height(16.dp))

        // لیست محصولات
        LazyColumn {
            items(
                items = products,
                key = { it.productId }  // برای Performance بهتر
            ) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = product.productName,
                                style = MaterialTheme.typography.titleMedium
                            )
                            Text(
                                text = "💰 ${product.productPrice} تومان",
                                style = MaterialTheme.typography.bodyMedium
                            )
                            Text(
                                text = "📦 موجودی: ${product.productStock}",
                                style = MaterialTheme.typography.bodySmall
                            )
                        }

                        // دکمه حذف
                        IconButton(
                            onClick = {
                                scope.launch {
                                    productDao.deleteProduct(product)
                                }
                            }
                        ) {
                            Text("🗑️")
                        }
                    }
                }
            }

            // اگه لیست خالیه
            if (products.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("هنوز محصولی اضافه نکردی! 😊")
                    }
                }
            }
        }
    }

    // ۹. Dialog اضافه کردن محصول
    if (showDialogState) {
        AlertDialog(
            onDismissRequest = {
                showDialogState = false
                productName = ""
                productPrice = ""
                productStock = ""
            },
            title = { Text("🆕 محصول جدید") },
            text = {
                Column {
                    OutlinedTextField(
                        value = productName,
                        onValueChange = { productName = it },
                        label = { Text("نام محصول") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = productPrice,
                        onValueChange = { productPrice = it },
                        label = { Text("قیمت (تومان)") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = productStock,
                        onValueChange = { productStock = it },
                        label = { Text("موجودی") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            // اضافه کردن به دیتابیس
                            productDao.insertProduct(
                                ProductEntity(
                                    productName = productName,
                                    productPrice = productPrice.toDoubleOrNull() ?: 0.0,
                                    productStock = productStock.toIntOrNull() ?: 0
                                )
                            )
                            showDialogState = false
                            productName = ""
                            productPrice = ""
                            productStock = ""
                        }
                    }
                ) {
                    Text("ذخیره ✅")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showDialogState = false
                        productName = ""
                        productPrice = ""
                        productStock = ""
                    }
                ) {
                    Text("انصراف ❌")
                }
            }
        )
    }

}