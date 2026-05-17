package com.mkarshnas6.karenstudio.worldskill.ui.screen.onlineShop

import android.content.Context
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MailOutline
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.gson.Gson
import com.mkarshnas6.karenstudio.worldskill.data.remote.RetrofitClient
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.ProductOnline
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.RegisterUserRequest
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.RegisterUserResponse
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.UpdateProductRequest
import com.mkarshnas6.karenstudio.worldskill.navigation.Screen
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant
import com.mkarshnas6.karenstudio.worldskill.utils.DataStoreManager
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okio.IOException

@Composable
fun OnlineShopScreen(
    navController: NavController, context: Context
) {

    val dataStore = remember { DataStoreManager(context) }

    var products by remember { mutableStateOf<List<ProductOnline>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val scope = rememberCoroutineScope()

    var showDialogRegister by remember { mutableStateOf(false) }
    var showDialogLogin by remember { mutableStateOf(false) }
    var showDialogDelete by remember { mutableStateOf(false) }
    var showDialogEdit by remember { mutableStateOf(false) }
    var dialogLoading by remember { mutableStateOf(false) }

    var productToDelete by remember { mutableStateOf<ProductOnline?>(null) }
    var productToEdit by remember { mutableStateOf<ProductOnline?>(null) }

    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var fullName by remember { mutableStateOf("") }

    val savedToken =
        dataStore.readStringFlow(AppConstant.DataStore.TOKEN, "").collectAsState(initial = "")

    var savedInfoUser by remember { mutableStateOf<RegisterUserResponse?>(null) }

    fun updateInfoUser() {
        scope.launch {
            try {
                val jsonString = dataStore.readString(AppConstant.DataStore.USER_REGISTER, "")
                val user = if (jsonString.isNotEmpty()) {
                    Gson().fromJson(jsonString, RegisterUserResponse::class.java)
                } else {
                    null
                }

                withContext(Dispatchers.Main) {
                    savedInfoUser = user
                    isLoading = false
                }

            } catch (e: IOException) {
                withContext(Dispatchers.Main) {
                    errorMessage = e.message
                    isLoading = false
                }
            }
        }
    }

    fun requestAllProducts() {
        scope.launch {
            try {
                isLoading = true
                val response = RetrofitClient.apiService.getAllProducts()
                if (response.isSuccessful) {
                    products = response.body() ?: emptyList()
                    errorMessage = null

                } else {
                    errorMessage = "Error : ${response.code()}"
                    Toast.makeText(context, "Server error: ${response.code()}", Toast.LENGTH_SHORT)
                        .show()
                }
                isLoading = false
            } catch (e: Exception) {
                Toast.makeText(context, "error in loading !!", Toast.LENGTH_SHORT).show()
                errorMessage = e.message
                isLoading = false
            }
        }
    }

    fun loginUser(user: RegisterUserRequest) {
        scope.launch {
            try {
                dialogLoading = true
                val response = RetrofitClient.apiService.loginUser(user.username, user.password)
                if (response.isSuccessful) {
                    val token = response.body()?.accessToken
                    if (!token.isNullOrBlank()) {
                        dataStore.saveString(AppConstant.DataStore.TOKEN, token)

                        val userInfoResponse =
                            RetrofitClient.apiService.getUserInfo("Bearer $token")
                        if (userInfoResponse.isSuccessful) {
                            val userInfo = userInfoResponse.body()
                            if (userInfo != null) {
                                dataStore.saveString(
                                    AppConstant.DataStore.USER_REGISTER,
                                    Gson().toJson(userInfo)
                                )
                                updateInfoUser()
                                Toast.makeText(
                                    context,
                                    "Login Success! Token: $token",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {
                            Toast.makeText(context, "Error in get User Info !", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        Toast.makeText(context, "Token is Empty !!", Toast.LENGTH_SHORT).show()
                    }
                    showDialogLogin = false
                } else {
                    Toast.makeText(context, "Login Failed: ${response.code()}", Toast.LENGTH_SHORT)
                        .show()
                }
                dialogLoading = false
            } catch (e: Exception) {
                Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                dialogLoading = false
            }
        }
    }

    fun registerUser(user: RegisterUserRequest) {
        scope.launch {
            dialogLoading = true
            try {
                Log.d("REGISTER", "Sending: ${Gson().toJson(user)}")
                val response = RetrofitClient.apiService.registerUser(user)
                if (response.isSuccessful) {
                    val userResponse = response.body()
                    if (userResponse != null) {
                        dataStore.saveString(
                            AppConstant.DataStore.USER_REGISTER,
                            Gson().toJson(userResponse)
                        )
                        loginUser(user)
                    }
                    updateInfoUser()
                    Toast.makeText(context, "Register is success !!", Toast.LENGTH_SHORT).show()
                    showDialogRegister = false
                } else {
                    Toast.makeText(
                        context,
                        "Register Failed : ${response.code()}",
                        Toast.LENGTH_SHORT
                    ).show()
                    dialogLoading = false
                }
                isLoading = false
            } catch (e: Exception) {
                Toast.makeText(context, "Error : ${e.message}", Toast.LENGTH_SHORT).show()
                isLoading = false
            }
        }
    }

    fun deleteProduct(productId: Int) {
        scope.launch {
            try {
                val response =
                    RetrofitClient.apiService.deleteProduct(productId, "Bearer ${savedToken.value}")
                if (response.isSuccessful) {
                    products = products.filter { it.id != productId }
                    Toast.makeText(
                        context,
                        response.body()?.message ?: "حذف شد ",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(context, "Erro in Delete ${response.code()}", Toast.LENGTH_SHORT)
                        .show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "Error : ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    fun updateProduct(updatedProduct: ProductOnline) {
        scope.launch {
            try {
                val request = UpdateProductRequest(
                    name = updatedProduct.name,
                    description = updatedProduct.description,
                    price = updatedProduct.price,
                    discountPrice = updatedProduct.discountPrice,
                    stock = updatedProduct.stock,
                    sku = updatedProduct.sku,
                    categoryId = updatedProduct.categoryId,
                    isAvailable = updatedProduct.isAvailable
                )

                val response = RetrofitClient.apiService.updateProduct(
                    productId = updatedProduct.id ?: 0,
                    updatedProductRequest = request,
                    token = "Bearer ${savedToken.value}"
                )

                if (response.isSuccessful) {
                    val updatedFromServer = response.body()
                    products = products.map {
                        if (it.id == updatedProduct.id) updatedFromServer ?: it else it
                    }
                    Toast.makeText(context, "آپدیت شد!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(context, "خطا: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {
                Toast.makeText(context, "خطا: ${e.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    LaunchedEffect(Unit) {
        requestAllProducts()
        updateInfoUser()
    }

    LaunchedEffect(errorMessage) {
        if (errorMessage != null) Log.e("ERROR_API", errorMessage.toString())
    }

    Box(modifier = Modifier.fillMaxSize()) {
        when {
            isLoading -> {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .align(Alignment.Center)
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(22.dp), color = Color.Black)
                    Spacer(modifier = Modifier.height(10.dp))
                    Text(
                        text = "Loading ...", color = Color.Black, fontSize = 20.sp
                    )
                }
            }

            errorMessage != null -> {
                Column(
                    modifier = Modifier.align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "error : $errorMessage",
                        color = MaterialTheme.colorScheme.error,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { requestAllProducts() },
                        modifier = Modifier
                            .background(Color.White, shape = CircleShape)
                            .clip(CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "ic refresh",
                            tint = Color.Black,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            products.isEmpty() -> {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Not find any product",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth(),
                        fontSize = 22.sp,
                        color = Color.LightGray
                    )
                }
            }

            else -> {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                ) {
                    Row(
                        modifier = Modifier
                            .padding(top = 40.dp, start = 10.dp, end = 10.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "Products", fontSize = 20.sp, color = Color.Black
                        )
                        when {
                            savedToken.value.isBlank() -> {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text(
                                        text = "Login",
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable { showDialogLogin = true }
                                            .padding(7.dp)
                                    )

                                    Text(
                                        text = "Register",
                                        fontSize = 18.sp,
                                        color = Color.Black,
                                        modifier = Modifier
                                            .clip(CircleShape)
                                            .clickable { showDialogRegister = true }
                                            .padding(7.dp)
                                    )
                                }
                            }

                            savedInfoUser != null -> {
                                savedInfoUser?.let { user ->
                                    Row {
                                        Icon(
                                            imageVector = Icons.Default.MailOutline,
                                            contentDescription = null,
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(80.dp)
                                                .clip(CircleShape)
                                                .padding(10.dp)
                                                .clickable {
                                                    navController.navigate(
                                                        Screen.ChatScreenWS.createRoute(
                                                            user.id
                                                        )
                                                    )
                                                }
                                        )
                                        Column {
                                            Text(
                                                text = user.full_name ?: user.username,
                                                color = Color.Black,
                                                fontSize = 16.sp,
                                                fontWeight = FontWeight.Bold
                                            )
                                            Text(
                                                text = "Logout",
                                                color = Color.Red,
                                                fontSize = 14.sp,
                                                modifier = Modifier.clickable {
                                                    scope.launch {
                                                        dataStore.removeByKey(AppConstant.DataStore.TOKEN)
                                                        dataStore.removeByKey(AppConstant.DataStore.USER_REGISTER)
                                                        savedInfoUser = null
                                                    }
                                                }
                                            )
                                        }
                                    }
                                }
                            }

                            else -> {
                                CircularProgressIndicator(modifier = Modifier.size(15.dp))
                            }
                        }

                    }
                    Spacer(modifier = Modifier.height(10.dp))
                    LazyColumn(
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f)
                    ) {
                        items(
                            count = products.size
                        ) { index ->
                            val product = products[index]
                            Box(
                                modifier = Modifier
                                    .padding(horizontal = 20.dp, vertical = 10.dp)
                                    .fillMaxWidth()
                                    .shadow(elevation = 10.dp, shape = RoundedCornerShape(14.dp))
                                    .background(Color.White, shape = RoundedCornerShape(14.dp))
                                    .height(130.dp)
                                    .clip(RoundedCornerShape(14.dp))
                            ) {
                                Column(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                    Text(
                                        text = product.name,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 24.sp,
                                        color = Color.Black
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .verticalScroll(
                                                rememberScrollState()
                                            )
                                    ) {
                                        Text(
                                            text = product.description ?: "no description",
                                            color = Color.DarkGray,
                                            fontSize = 17.sp,
                                            textAlign = TextAlign.Center,
                                            modifier = Modifier
                                                .padding(5.dp)
                                                .fillMaxSize(),
                                            maxLines = 3,
                                        )
                                    }
                                    Row(
                                        modifier = Modifier
                                            .padding(start = 10.dp)
                                            .fillMaxWidth()
                                    ) {
                                        Text(
                                            text = product.price.toString(),
                                            textAlign = TextAlign.Start,
                                            color = Color.Gray,
                                            fontSize = 20.sp
                                        )
                                    }
                                }
                                Box(
                                    modifier = Modifier
                                        .padding(10.dp)
                                        .fillMaxSize()
                                ) {
                                    Row(modifier = Modifier.align(Alignment.TopEnd)) {

                                        Icon(
                                            imageVector = Icons.Default.Edit,
                                            contentDescription = "ic edit",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(23.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    productToEdit = product
                                                    showDialogEdit = true
                                                }
                                        )

                                        Icon(
                                            imageVector = Icons.Default.Delete,
                                            contentDescription = "ic delete",
                                            tint = Color.Black,
                                            modifier = Modifier
                                                .size(23.dp)
                                                .clip(CircleShape)
                                                .clickable {
                                                    productToDelete = product
                                                    showDialogDelete = true
                                                }
                                        )

                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    if (showDialogEdit) {
        productToEdit?.let { product ->
            EditProductDialog(
                product,
                false,
                onDismiss = { productToEdit = null;showDialogEdit = false },
                onSaveClick = { updateProduct(product);showDialogEdit = false }
            )
        }
    }

    if (showDialogDelete) {
        DeleteDialog(
            onDismiss = { showDialogDelete = false;productToDelete = null },
            onDeleteClick = {
                if (productToDelete != null) {
                    productToDelete?.let { product ->
                        deleteProduct(product.id)
                    }
                    productToDelete = null
                    showDialogDelete = false
                }
            }
        )
    }

    if (showDialogLogin) {
        LoginDialog(
            username = username,
            password = password,
            isLoading = dialogLoading,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onLoginClick = {
                val userInfo = RegisterUserRequest(
                    username = username,
                    password = password,
                    email = email,
                    full_name = fullName
                )
                loginUser(userInfo)
            },
            onDismiss = { showDialogLogin = false },
            onSwitchToRegister = {
                showDialogLogin = false
                showDialogRegister = true
            })
    }

    if (showDialogRegister) {
        RegisterDialog(
            username = username,
            password = password,
            email = email,
            fullName = fullName,
            isLoading = dialogLoading,
            onUsernameChange = { username = it },
            onPasswordChange = { password = it },
            onEmailChange = { email = it },
            onFullNameChange = { fullName = it },
            onRegisterClick = {
                scope.launch {
                    val userInfo = RegisterUserRequest(
                        username = username,
                        password = password,
                        email = email,
                        full_name = fullName
                    )
                    registerUser(userInfo)
                }
            },
            onDismiss = { showDialogRegister = false },
            onSwitchToLogin = {
                showDialogRegister = false
                showDialogLogin = true
            })
    }

}

@Composable
fun DeleteDialog(
    onDeleteClick: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "Delete", fontWeight = FontWeight.Bold, fontSize = 22.sp
        )
    }, text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Are you sure for delete Product ?? "
            )
        }
    }, confirmButton = {
        Button(
            onClick = onDeleteClick,
            colors = ButtonDefaults.buttonColors(
                contentColor = Color.White,
                containerColor = Color.Red
            ),
            modifier = Modifier
                .clip(CircleShape)
        ) {
            Text(
                text = "Delete",
                color = Color.White,
                fontSize = 22.sp
            )
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    })
}

@Composable
fun EditProductDialog(
    product: ProductOnline,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onSaveClick: (ProductOnline) -> Unit
) {
    var name by remember { mutableStateOf(product.name ?: "") }
    var description by remember { mutableStateOf(product.description ?: "") }
    var price by remember { mutableStateOf(product.price?.toString() ?: "") }
    var stock by remember { mutableStateOf(product.stock?.toString() ?: "") }
    var isAvailable by remember { mutableStateOf(product.isAvailable ?: true) }

    AlertDialog(
        onDismissRequest = { if (!isLoading) onDismiss() },
        title = {
            Text(
                text = "Edit Product",
                fontWeight = FontWeight.Bold,
                fontSize = 22.sp
            )
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // نام محصول
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Product Name") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading
                )

                // توضیحات
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 3,
                    enabled = !isLoading
                )

                // قیمت
                OutlinedTextField(
                    value = price,
                    onValueChange = { newValue ->
                        // فقط عدد و نقطه قبول کن
                        if (newValue.isEmpty() || newValue.matches(Regex("^\\d*\\.?\\d*$"))) {
                            price = newValue
                        }
                    },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal)
                )

                // موجودی
                OutlinedTextField(
                    value = stock,
                    onValueChange = { newValue ->
                        // فقط عدد صحیح قبول کن
                        if (newValue.isEmpty() || newValue.all { it.isDigit() }) {
                            stock = newValue
                        }
                    },
                    label = { Text("Stock") },
                    modifier = Modifier.fillMaxWidth(),
                    singleLine = true,
                    enabled = !isLoading,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )

                // وضعیت موجود بودن
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "Available",
                        fontSize = 16.sp,
                        color = Color.Black
                    )
                    Switch(
                        checked = isAvailable,
                        onCheckedChange = { isAvailable = it },
                        enabled = !isLoading
                    )
                }

                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(Alignment.CenterHorizontally)
                            .size(24.dp)
                    )
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val updatedProduct = product.copy(
                        name = name.ifBlank { product.name },
                        description = description.ifBlank { null },
                        price = price.toDoubleOrNull() ?: product.price,
                        stock = stock.toIntOrNull() ?: product.stock,
                        isAvailable = isAvailable
                    )
                    onSaveClick(updatedProduct)
                },
                enabled = !isLoading && name.isNotBlank()
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss,
                enabled = !isLoading
            ) {
                Text("Cancel")
            }
        }
    )
}

@Composable
fun LoginDialog(
    username: String,
    password: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onLoginClick: () -> Unit,
    onDismiss: () -> Unit,
    onSwitchToRegister: () -> Unit
) {
    androidx.compose.material3.AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "Login", fontWeight = FontWeight.Bold, fontSize = 22.sp
        )
    }, text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(24.dp)
                )
            }
        }
    }, confirmButton = {
        Button(
            onClick = onLoginClick,
            enabled = !isLoading && username.isNotBlank() && password.isNotBlank()
        ) {
            Text("Login")
        }
    }, dismissButton = {
        androidx.compose.material3.TextButton(onClick = onSwitchToRegister) {
            Text("Register")
        }
    })
}

@Composable
fun RegisterDialog(
    username: String,
    password: String,
    email: String,
    fullName: String,
    isLoading: Boolean,
    onUsernameChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onFullNameChange: (String) -> Unit,
    onRegisterClick: () -> Unit,
    onDismiss: () -> Unit,
    onSwitchToLogin: () -> Unit
) {
    androidx.compose.material3.AlertDialog(onDismissRequest = onDismiss, title = {
        Text(
            text = "Register", fontWeight = FontWeight.Bold, fontSize = 22.sp
        )
    }, text = {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            androidx.compose.material3.OutlinedTextField(
                value = fullName,
                onValueChange = onFullNameChange,
                label = { Text("Full Name") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            androidx.compose.material3.OutlinedTextField(
                value = email,
                onValueChange = onEmailChange,
                label = { Text("Email") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            androidx.compose.material3.OutlinedTextField(
                value = username,
                onValueChange = onUsernameChange,
                label = { Text("Username") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            androidx.compose.material3.OutlinedTextField(
                value = password,
                onValueChange = onPasswordChange,
                label = { Text("Password") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                enabled = !isLoading
            )
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .size(24.dp)
                )
            }
        }
    }, confirmButton = {
        Button(
            onClick = onRegisterClick,
            enabled = !isLoading && username.isNotBlank() && password.isNotBlank() && email.isNotBlank()
        ) {
            Text("Register")
        }
    }, dismissButton = {
        androidx.compose.material3.TextButton(onClick = onSwitchToLogin) {
            Text("Back to Login")
        }
    })
}