package com.mkarshnas6.karenstudio.worldskill.ui.screen.testUI

import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mkarshnas6.karenstudio.worldskill.R
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// ============================
// مدل داده
// ============================
data class Product(
    val id: Int,
    val name: String,
    val price: Double,
    val category: String,
    val inStock: Boolean
)

data class User(
    val username: String,
    val email: String,
    val age: Int,
    val country: String
)

// ============================
// UI اصلی برای تست
// ============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TestUiScreen() {
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val listState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val focusManager = LocalFocusManager.current

    // State های مختلف
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var username by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var searchQuery by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf("All") }
    var isLoading by remember { mutableStateOf(false) }
    var showDialog by remember { mutableStateOf(false) }
    var showBottomSheet by remember { mutableStateOf(false) }
    var showDeleteConfirm by remember { mutableStateOf(false) }
    var passwordVisible by remember { mutableStateOf(false) }
    var rememberMe by remember { mutableStateOf(false) }
    var agreeTerms by remember { mutableStateOf(false) }
    var selectedGender by remember { mutableStateOf("") }
    var ratingValue by remember { mutableStateOf(0f) }
    var sliderValue by remember { mutableStateOf(50f) }
    var expanded by remember { mutableStateOf(false) }
    var selectedCountry by remember { mutableStateOf("") }
    var addedToCart by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var successMessage by remember { mutableStateOf<String?>(null) }
    var navigateToDetail by remember { mutableStateOf(false) }
    var deleteItemId by remember { mutableStateOf<Int?>(null) }
    var selectedTabIndex by remember { mutableStateOf(0) }
    var isRefreshing by remember { mutableStateOf(false) }
    var fabClicked by remember { mutableStateOf(false) }

    // محصولات نمونه
    val allProducts = remember {
        listOf(
            Product(1, "Laptop Gaming Pro", 1299.99, "Electronics", true),
            Product(2, "Wireless Headphones", 79.99, "Electronics", true),
            Product(3, "Coffee Maker", 49.99, "Home", true),
            Product(4, "Running Shoes", 89.99, "Sports", false),
            Product(5, "Smart Watch", 199.99, "Electronics", true),
            Product(6, "Desk Lamp", 29.99, "Home", true),
            Product(7, "Yoga Mat", 24.99, "Sports", true),
            Product(8, "Bluetooth Speaker", 59.99, "Electronics", false),
            Product(9, "Water Bottle", 14.99, "Sports", true),
            Product(10, "Notebook Set", 12.99, "Office", true),
            Product(11, "Backpack", 44.99, "Fashion", true),
            Product(12, "Sunglasses", 34.99, "Fashion", true),
            Product(13, "Mechanical Keyboard", 149.99, "Electronics", true),
            Product(14, "Plant Pot", 19.99, "Home", false),
            Product(15, "Protein Powder", 39.99, "Sports", true),
        )
    }

    val categories = listOf("All", "Electronics", "Home", "Sports", "Fashion", "Office")

    // فیلتر محصولات
    val filteredProducts = remember(searchQuery, selectedCategory) {
        allProducts.filter { product ->
            (selectedCategory == "All" || product.category == selectedCategory) &&
                    (searchQuery.isEmpty() || product.name.contains(searchQuery, ignoreCase = true))
        }
    }

    // کشورها
    val countries = listOf(
        "Iran", "USA", "Germany", "Japan", "France",
        "Canada", "Brazil", "Australia", "India", "South Korea"
    )

    // ============================
    // Snackbar Host
    // ============================
    Scaffold(
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    snackbarData = data,
                    containerColor = if (data.visuals.message.contains("Error"))
                        MaterialTheme.colorScheme.error
                    else
                        MaterialTheme.colorScheme.primary,
                    modifier = Modifier.testTag("Snackbar_TabProfile")
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    fabClicked = true
                    scope.launch {
                        snackbarHostState.showSnackbar("FAB Clicked!")
                    }
                }
            ) {
                Icon(Icons.Default.ShoppingCart, contentDescription = "Shopping Cart")
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // ============================
            // Tab Row
            // ============================
            TabRow(
                selectedTabIndex = selectedTabIndex
            ) {
                Tab(
                    selected = selectedTabIndex == 0,
                    onClick = { selectedTabIndex = 0 },
                    text = { Text("Products") },
                    modifier = Modifier.testTag("tab_Products")
                )
                Tab(
                    selected = selectedTabIndex == 1,
                    onClick = { selectedTabIndex = 1 },
                    text = { Text("Profile") },
                    modifier = Modifier.testTag("tab_Profile")
                )
                Tab(
                    selected = selectedTabIndex == 2,
                    onClick = { selectedTabIndex = 2 },
                    text = { Text("Settings") },
                    modifier = Modifier.testTag("tab_Settings")
                )
            }

            // ============================
            // محتوای تب‌ها
            // ============================
            when (selectedTabIndex) {
                0 -> ProductsTab(
                    filteredProducts = filteredProducts,
                    searchQuery = searchQuery,
                    onSearchChange = { searchQuery = it },
                    categories = categories,
                    selectedCategory = selectedCategory,
                    onCategorySelected = { selectedCategory = it },
                    isLoading = isLoading,
                    isRefreshing = isRefreshing,
                    onRefresh = {
                        scope.launch {
                            isRefreshing = true
                            delay(2000)
                            isRefreshing = false
                        }
                    },
                    onProductClick = { product ->
                        scope.launch {
                            snackbarHostState.showSnackbar("Clicked: ${product.name}")
                        }
                    },
                    onDeleteClick = { product ->
                        deleteItemId = product.id
                        showDeleteConfirm = true
                    },
                    onAddToCart = { product ->
                        addedToCart = true
                        scope.launch {
                            snackbarHostState.showSnackbar("${product.name} added to cart!")
                        }
                    },
                    listState = listState
                )

                1 -> ProfileTab(
                    email = email,
                    onEmailChange = { email = it },
                    password = password,
                    onPasswordChange = { password = it },
                    username = username,
                    onUsernameChange = { username = it },
                    age = age,
                    onAgeChange = { age = it },
                    rememberMe = rememberMe,
                    onRememberMeChange = { rememberMe = it },
                    agreeTerms = agreeTerms,
                    onAgreeTermsChange = { agreeTerms = it },
                    passwordVisible = passwordVisible,
                    onPasswordVisibilityChange = { passwordVisible = it },
                    selectedGender = selectedGender,
                    onGenderSelected = { selectedGender = it },
                    errorMessage = errorMessage,
                    onLoginClick = {
                        isLoading = true
                        errorMessage = null
                        successMessage = null
                        scope.launch {
                            delay(3000)
                            isLoading = false
                            if (email.isEmpty() || password.isEmpty()) {
                                errorMessage = "Email and password are required"
                            } else if (!email.contains("@")) {
                                errorMessage = "Invalid email format"
                            } else if (password.length < 6) {
                                errorMessage = "Password must be at least 6 characters"
                            } else {
                                successMessage = "Login successful! Welcome $email"
                                scope.launch {
                                    snackbarHostState.showSnackbar("Welcome $email!")
                                }
                            }
                        }
                    },
                    isLoading = isLoading
                )

                2 -> SettingsTab(
                    sliderValue = sliderValue,
                    onSliderChange = { sliderValue = it },
                    ratingValue = ratingValue,
                    onRatingChange = { ratingValue = it },
                    expanded = expanded,
                    onExpandedChange = { expanded = it },
                    selectedCountry = selectedCountry,
                    onCountrySelected = { selectedCountry = it },
                    countries = countries,
                    showBottomSheet = showBottomSheet,
                    onShowBottomSheet = { showBottomSheet = true },
                    onDismissBottomSheet = { showBottomSheet = false }
                )
            }
        }
    }

    // ============================
    // دیالوگ حذف
    // ============================
    if (showDeleteConfirm && deleteItemId != null) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirm = false },
            title = { Text("Delete Item") },
            text = { Text("Are you sure you want to delete this item? This action cannot be undone.") },
            confirmButton = {
                TextButton(
                    onClick = {
                        showDeleteConfirm = false
                        deleteItemId = null
                        scope.launch {
                            snackbarHostState.showSnackbar("Item deleted successfully")
                        }
                    },
                    modifier = Modifier.testTag("btn_ConfirmDeleteProduct")
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirm = false },
                    modifier = Modifier.testTag("btn_CancelDeleteProduct")
                ) {
                    Text("Cancel")
                }
            },
            modifier = Modifier.testTag("DialogDelete_TabProfile")
        )
    }

    // ============================
    // Bottom Sheet
    // ============================
    if (showBottomSheet) {
        ModalBottomSheet(
            onDismissRequest = { showBottomSheet = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    "Select Country",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(16.dp))
                countries.forEach { country ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                selectedCountry = country
                                showBottomSheet = false
                            }
                            .padding(vertical = 12.dp)
                    ) {
                        RadioButton(
                            selected = selectedCountry == country,
                            onClick = {
                                selectedCountry = country
                                showBottomSheet = false
                            }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(country, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // ============================
    // Navigation Detail (شبیه‌سازی)
    // ============================
    if (navigateToDetail) {
        // اینجا می‌تونیم یه صفحه دیگه رو نشون بدیم
        // برای تست، فقط یه Surface با متن نشون میدیم
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text("Detail Screen", fontSize = 24.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(16.dp))
                Button(onClick = { navigateToDetail = false }) {
                    Text("Back")
                }
            }
        }
    }
}

// ============================
// تب محصولات
// ============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductsTab(
    filteredProducts: List<Product>,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    categories: List<String>,
    selectedCategory: String,
    onCategorySelected: (String) -> Unit,
    isLoading: Boolean,
    isRefreshing: Boolean,
    onRefresh: () -> Unit,
    onProductClick: (Product) -> Unit,
    onDeleteClick: (Product) -> Unit,
    onAddToCart: (Product) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    Column(modifier = Modifier.fillMaxSize()) {
        // سرچ بار
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp)
                .testTag("TextFieldSearchProduct"),
            placeholder = { Text("Search products...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = "Search") },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(
                        onClick = { onSearchChange("") },
                        modifier = Modifier.testTag("btn_delete_search")
                    ) {
                        Icon(Icons.Default.Close, contentDescription = "Clear search")
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                // عمل سرچ
            })
        )

        // کتگوری‌ها (LazyRow)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(categories) { category ->
                FilterChip(
                    selected = selectedCategory == category,
                    onClick = { onCategorySelected(category) },
                    label = { Text(category) },
                    modifier = Modifier.testTag("category_product_${category}")
                )
            }
        }

        // لودینگ
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(8.dp))
                    Text("Loading products...", color = Color.Gray)
                }
            }
        } else if (isRefreshing) {
            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
        }

        // لیست محصولات
        if (filteredProducts.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    "No products found",
                    style = MaterialTheme.typography.bodyLarge,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )
            }
        } else {
            LazyColumn(
                state = listState,
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredProducts, key = { it.id }) { product ->
                    ProductCard(
                        product = product,
                        onClick = { onProductClick(product) },
                        onDelete = { onDeleteClick(product) },
                        onAddToCart = { onAddToCart(product) },
                    )
                }
                // آیتم آخر برای اسکرول
                item {
                    Spacer(modifier = Modifier.height(80.dp))
                    Text(
                        "End of list",
                        modifier = Modifier.fillMaxWidth(),
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}

// ============================
// کارت محصول
// ============================
@Composable
fun ProductCard(
    product: Product,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    onAddToCart: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("product_item_${product.id}")
            .testTag("product_name_${product.name.replace(" ", "_")}"),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
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
                    text = product.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "$${product.price}",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = product.category,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    if (product.inStock) {
                        Text(
                            "In Stock",
                            color = Color(0xFF4CAF50),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    } else {
                        Text(
                            "Out of Stock",
                            color = Color(0xFFF44336),
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }
            Row {
                IconButton(onClick = onAddToCart) {
                    Icon(
                        Icons.Default.Add,
                        contentDescription = "Add to cart",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }

                IconButton(
                    modifier = Modifier.testTag("btn_DeleteProduct_${product.id}"),
                    onClick = onDelete,
                ) {
                    Icon(
                        Icons.Default.Delete,
                        contentDescription = "Delete product",
                        tint = MaterialTheme.colorScheme.error,
                    )
                }
            }
        }
    }
}

// ============================
// تب پروفایل (فرم لاگین)
// ============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTab(
    email: String,
    onEmailChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    username: String,
    onUsernameChange: (String) -> Unit,
    age: String,
    onAgeChange: (String) -> Unit,
    rememberMe: Boolean,
    onRememberMeChange: (Boolean) -> Unit,
    agreeTerms: Boolean,
    onAgreeTermsChange: (Boolean) -> Unit,
    passwordVisible: Boolean,
    onPasswordVisibilityChange: (Boolean) -> Unit,
    selectedGender: String,
    onGenderSelected: (String) -> Unit,
    errorMessage: String?,
    onLoginClick: () -> Unit,
    isLoading: Boolean
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            "Login",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.testTag("TextTitleTabProfile")
        )

        // Username
        OutlinedTextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TextField_UsernameTabProfile"),
            singleLine = true,
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        // Email
        OutlinedTextField(
            value = email,
            onValueChange = onEmailChange,
            label = { Text("Email") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TextField_EmailTabProfile"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Email,
                imeAction = ImeAction.Next
            ),
            leadingIcon = { Icon(Icons.Default.Email, contentDescription = null) },
            isError = errorMessage != null && errorMessage.contains("email", ignoreCase = true)
        )

        // Password
        OutlinedTextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text("Password") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TextField_PasswordTabProfile"),
            singleLine = true,
            visualTransformation = if (passwordVisible)
                VisualTransformation.None
            else
                PasswordVisualTransformation(),
            keyboardOptions = KeyboardOptions(
                keyboardType = KeyboardType.Password,
                imeAction = ImeAction.Done
            ),
            leadingIcon = { Icon(Icons.Default.Lock, contentDescription = null) },
            trailingIcon = {
                IconButton(onClick = { onPasswordVisibilityChange(!passwordVisible) }) {
                    Icon(
                        painter = painterResource(
                            if (passwordVisible) R.drawable.ic_visibility
                            else R.drawable.ic_visibility_off
                        ),
                        contentDescription = if (passwordVisible) "Hide password"
                        else "Show password"
                    )
                }
            },
            isError = errorMessage != null && errorMessage.contains("password", ignoreCase = true)
        )

        // Age
        OutlinedTextField(
            value = age,
            onValueChange = onAgeChange,
            label = { Text("Age") },
            modifier = Modifier
                .fillMaxWidth()
                .testTag("TextField_AgeTabProfile"),
            singleLine = true,
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            leadingIcon = { Icon(Icons.Default.Person, contentDescription = null) }
        )

        // Gender
        Text("Gender", style = MaterialTheme.typography.titleSmall)
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Row(
                modifier = Modifier
                    .clickable { onGenderSelected("Male") }
                    .testTag("btn_GenderMale"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedGender == "Male",
                    onClick = { onGenderSelected("Male") }
                )
                Text("Male")
            }
            Row(
                modifier = Modifier
                    .clickable { onGenderSelected("Female") }
                    .testTag("btn_GenderFemale"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedGender == "Female",
                    onClick = { onGenderSelected("Female") }
                )
                Text("Female")
            }
            Row(
                modifier = Modifier
                    .clickable { onGenderSelected("Other") }
                    .testTag("btn_GenderAnother"),
                verticalAlignment = Alignment.CenterVertically
            ) {
                RadioButton(
                    selected = selectedGender == "Other",
                    onClick = { onGenderSelected("Other") }
                )
                Text("Other")
            }
        }

        // Remember Me
        Row(
            modifier = Modifier.clickable { onRememberMeChange(!rememberMe) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = rememberMe,
                onCheckedChange = onRememberMeChange
            )
            Text("Remember Me")
        }

        // Agree Terms
        Row(
            modifier = Modifier
                .clickable { onAgreeTermsChange(!agreeTerms) }
                .testTag("btn_AgreeTermsTabProfile"),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Checkbox(
                checked = agreeTerms,
                onCheckedChange = onAgreeTermsChange
            )
            Text("I agree to the Terms and Conditions")
        }

        // Error Message
        AnimatedVisibility(visible = errorMessage != null) {
            errorMessage?.let {
                Text(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.testTag("text_MessageErrorTabProfile")
                )
            }
        }

        // Login Button
        Button(
            onClick = onLoginClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("btn_LoginTabProfile"),
            enabled = agreeTerms && !isLoading
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(24.dp)
                        .testTag("loading_BtnLoginTabProfile"),
                    color = Color.White
                )
            } else {
                Text("Login", fontSize = 16.sp)
            }
        }

        // Loading Text
        if (isLoading) {
            Text(
                "Please wait...",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                color = Color.Gray
            )
        }
    }
}

// ============================
// تب تنظیمات
// ============================
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsTab(
    sliderValue: Float,
    onSliderChange: (Float) -> Unit,
    ratingValue: Float,
    onRatingChange: (Float) -> Unit,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    selectedCountry: String,
    onCountrySelected: (String) -> Unit,
    countries: List<String>,
    showBottomSheet: Boolean,
    onShowBottomSheet: () -> Unit,
    onDismissBottomSheet: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            "Settings",
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        // Volume Slider
        Text("Volume: ${sliderValue.toInt()}%")
        Slider(
            value = sliderValue,
            onValueChange = onSliderChange,
            valueRange = 0f..100f,
            steps = 9
        )

        // Brightness Slider
        Text("Brightness")
        Slider(
            value = sliderValue,
            onValueChange = onSliderChange,
            modifier = Modifier.fillMaxWidth()
        )

        // Rating
        Text("Rate this app")
        Row {
            for (i in 1..5) {
                Icon(
                    Icons.Default.Star,
                    contentDescription = "Star $i",
                    modifier = Modifier
                        .size(40.dp)
                        .clickable { onRatingChange(i.toFloat()) },
                    tint = if (i <= ratingValue) Color(0xFFFFC107) else Color.Gray
                )
            }
        }

        // Dropdown (ExposedDropdownMenu)
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = onExpandedChange
        ) {
            OutlinedTextField(
                value = selectedCountry.ifEmpty { "Select country..." },
                onValueChange = {},
                readOnly = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text("Country") },
                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { onExpandedChange(false) }
            ) {
                countries.forEach { country ->
                    DropdownMenuItem(
                        text = { Text(country) },
                        onClick = {
                            onCountrySelected(country)
                            onExpandedChange(false)
                        }
                    )
                }
            }
        }

        // Show Bottom Sheet Button
        Button(
            onClick = onShowBottomSheet,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Select Country (Bottom Sheet)")
        }

        // Switch for Notification
        var notificationEnabled by remember { mutableStateOf(true) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Enable Notifications")
            Switch(
                checked = notificationEnabled,
                onCheckedChange = { notificationEnabled = it }
            )
        }

        // Switch for Dark Mode
        var darkMode by remember { mutableStateOf(false) }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Dark Mode")
            Switch(
                checked = darkMode,
                onCheckedChange = { darkMode = it }
            )
        }
    }
}

