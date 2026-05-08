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
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
    var productName by remember { mutableStateOf("33") }
    var productPrice by remember { mutableStateOf("234") }
    var productStock by remember { mutableStateOf("342") }
    var searchQuery by remember { mutableStateOf("") }

    val pageSize = 10

    var isLoadingProducts by remember { mutableStateOf(false) }
    var isLastPage by remember { mutableStateOf(false) }
    var currentPage by remember { mutableIntStateOf(0) }

    // Inventory States
    var showInventoryDialog by remember { mutableStateOf(false) }
    var currentProductForInventory by remember { mutableStateOf<ProductEntity?>(null) }
    var inventoryCity by remember { mutableStateOf("") }
    var inventoryCount by remember { mutableStateOf("") }

    // list state for auto load
    val listState = rememberLazyListState()
    val shouldLoadMore by remember {
        derivedStateOf {
            val lastVisibleItems = listState.layoutInfo.visibleItemsInfo.lastOrNull()
            val lastVisibleIndex = lastVisibleItems?.index ?: 0
            val totalItems = listState.layoutInfo.totalItemsCount
            val nearEnded = lastVisibleIndex >= totalItems - 4
            nearEnded && totalItems > 0
        }
    }

    fun loadFirstPage() {
        scope.launch {
            isLoadingProducts = true
            val offset = 0
            val items = if (searchQuery.isBlank()) {
                productDao.getProductsPage(offset, pageSize)
            } else {
                productDao.searchProducts(searchQuery, offset, pageSize)
            }
            products = items
            currentPage = 0
            isLastPage = items.size < pageSize
            isLoadingProducts = false
        }
    }

    fun loadNextPage() {
        if (isLoadingProducts || isLastPage) return
        scope.launch {
            isLoadingProducts = true  // ← درست شد
            val nextPage = currentPage + 1
            val offset = nextPage * pageSize
            val items = if (searchQuery.isBlank()) {  // ← isBlank
                productDao.getProductsPage(offset, pageSize)
            } else {
                productDao.searchProducts(searchQuery, offset, pageSize)  // ← searchProductsPage
            }
            products = products + items
            currentPage = nextPage
            isLastPage = items.size < pageSize
            isLoadingProducts = false
        }
    }

    fun onSearch(query: String) {
        searchQuery = query
        loadFirstPage()
    }

    LaunchedEffect(Unit) {
        loadFirstPage()
    }

    LaunchedEffect(shouldLoadMore) {
        if (shouldLoadMore && !isLoadingProducts && !isLastPage) {
            loadNextPage()
        }
    }

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
            value = searchQuery,
            onValueChange = { query ->
                onSearch(query)
            },
            label = {
                Text(
                    "جستجو",
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

        LazyColumn(
            state = listState
        ) {
            // ========== حالت خالی ==========
            if (products.isEmpty() && !isLoadingProducts) {
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

            // ========== محصولات ==========
            items(
                items = products,
                key = { it.productId }
            ) { product ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
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

                                if (product.inventory.isNotEmpty()) {
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = "🏙️ موجودی شهرها:",
                                        style = MaterialTheme.typography.bodySmall,
                                        fontWeight = FontWeight.Bold
                                    )
                                    product.inventory.forEach { (city, count) ->
                                        Text(
                                            text = "  • $city: $count عدد",
                                            style = MaterialTheme.typography.bodySmall,
                                            color = Color.Gray
                                        )
                                    }
                                }
                            }

                            Column {
                                IconButton(onClick = {
                                    currentProductForInventory = product
                                    showInventoryDialog = true
                                }) {
                                    Text("🏙️")
                                }
                                IconButton(onClick = {
                                    scope.launch {
                                        productDao.deleteProduct(product)
                                        loadFirstPage()
                                    }
                                }) {
                                    Text("🗑️")
                                }
                            }
                        }
                    }
                }
            }

            // ========== لودینگ ==========
            if (isLoadingProducts) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⏳ در حال بارگذاری...")
                    }
                }
            }

            // ========== انتهای لیست ==========
            if (isLastPage && products.isNotEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("🏁 به انتهای لیست رسیدی!")
                    }
                }
            }
        }
    }

    // Dialog اضافه کردن محصول
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
                        label = { Text("موجودی کل") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        scope.launch {
                            productDao.insertProduct(
                                ProductEntity(
                                    productName = productName,
                                    productPrice = productPrice.toDoubleOrNull() ?: 0.0,
                                    productStock = productStock.toIntOrNull() ?: 0,
                                    inventory = emptyMap()
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
                TextButton(onClick = {
                    showDialogState = false
                    productName = ""
                    productPrice = ""
                    productStock = ""
                }) {
                    Text("انصراف ❌")
                }
            }
        )
    }

    // Dialog مدیریت Inventory
    if (showInventoryDialog && currentProductForInventory != null) {
        AlertDialog(
            onDismissRequest = {
                showInventoryDialog = false
                inventoryCity = ""
                inventoryCount = ""
            },
            title = { Text("🏙️ موجودی شهرها - ${currentProductForInventory!!.productName}") },
            text = {
                Column(
                    modifier = Modifier
                        .sizeIn(maxHeight = 500.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    val currentInventory = currentProductForInventory!!.inventory
                    if (currentInventory.isNotEmpty()) {
                        Text("موجودی فعلی:", fontWeight = FontWeight.Bold)
                        currentInventory.forEach { (city, count) ->
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text("• $city: $count عدد")
                                TextButton(onClick = {
                                    val updatedMap = currentInventory.toMutableMap()
                                    updatedMap.remove(city)
                                    scope.launch {
                                        productDao.updateProduct(
                                            currentProductForInventory!!.copy(inventory = updatedMap)
                                        )
                                        currentProductForInventory =
                                            currentProductForInventory!!.copy(inventory = updatedMap)
                                    }
                                }) {
                                    Text("❌", color = Color.Red)
                                }
                            }
                        }
                        Spacer(modifier = Modifier.height(8.dp))
                        HorizontalDivider()
                        Spacer(modifier = Modifier.height(8.dp))
                    }

                    OutlinedTextField(
                        value = inventoryCity,
                        onValueChange = { inventoryCity = it },
                        label = { Text("نام شهر") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = inventoryCount,
                        onValueChange = { inventoryCount = it },
                        label = { Text("تعداد") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (inventoryCity.isNotBlank() && inventoryCount.isNotBlank()) {
                        val count = inventoryCount.toIntOrNull() ?: 0
                        val currentInventory = currentProductForInventory!!.inventory.toMutableMap()
                        currentInventory[inventoryCity] = count

                        scope.launch {
                            productDao.updateProduct(
                                currentProductForInventory!!.copy(inventory = currentInventory)
                            )
                            currentProductForInventory =
                                currentProductForInventory!!.copy(inventory = currentInventory)
                            inventoryCity = ""
                            inventoryCount = ""
                        }
                    }
                }) {
                    Text("افزودن ✅")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showInventoryDialog = false
                    inventoryCity = ""
                    inventoryCount = ""
                }) {
                    Text("بستن")
                }
            }
        )
    }
}