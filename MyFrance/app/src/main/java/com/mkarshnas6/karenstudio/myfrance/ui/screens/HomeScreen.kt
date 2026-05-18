package com.mkarshnas6.karenstudio.myfrance.ui.screens

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.sizeIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.rememberLazyStaggeredGridState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.mkarshnas6.karenstudio.myfrance.R
import com.mkarshnas6.karenstudio.myfrance.data.local.dataStore.dataStoreManager
import com.mkarshnas6.karenstudio.myfrance.data.remote.RetrofitClient
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.DiaryDetail
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.InsertFavoriteRequest
import com.mkarshnas6.karenstudio.myfrance.navigation.Screen
import kotlinx.coroutines.launch

@SuppressLint("ConfigurationScreenWidthHeight")
@Composable
fun HomeScreen(
    navController: NavController,
    context: Context
) {
    var isSelectedDiary by remember { mutableStateOf(false) }
    var showLoading by remember { mutableStateOf(false) }
    var showLoadingDetails by remember { mutableStateOf(false) }

    val baseUrl by remember { mutableStateOf(RetrofitClient.BASE_URL) }

    val scope = rememberCoroutineScope()

    // get data store
    val dataStore = dataStoreManager(context)
    // get screen width and heigh
    val configuration = LocalConfiguration.current
    val screenWidth by remember { mutableStateOf(configuration.screenWidthDp) }
    val screenHeigh by remember { mutableStateOf(configuration.screenHeightDp) }

    val retrofitClient = RetrofitClient.apiService

    var listAllDiarys by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var listMyFavoritesDiarys by remember { mutableStateOf<List<Map<String, String>>>(emptyList()) }
    var diarySelectedDetail by remember { mutableStateOf<DiaryDetail?>(null) }

//    get token
    var tokenSaved by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        showLoading = true
        val responseAllDiarys = retrofitClient.getAllDiary()
        if (responseAllDiarys.isSuccessful) {
            listAllDiarys = responseAllDiarys.body()?.data ?: emptyList()
        } else {
            listAllDiarys = emptyList()
            Toast.makeText(
                context,
                "Error To Load Data - status code : ${responseAllDiarys.code()}!!",
                Toast.LENGTH_SHORT
            ).show()
        }

        // load token
        scope.launch {
            tokenSaved = dataStore.readString("auth_token", "")
        }

        if (tokenSaved.isNotBlank() && listAllDiarys.isNotEmpty()) {
            val responseAllFavorites = retrofitClient.getMyFavorites(tokenSaved)

            if (responseAllFavorites.isSuccessful) {
                listMyFavoritesDiarys = responseAllFavorites.body()?.data ?: emptyList()
            } else {
                Toast.makeText(
                    context,
                    "Error to load favorites : ${responseAllFavorites.code()}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    }

    Row(
        modifier = Modifier.fillMaxSize(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // show diaries
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .background(Color.LightGray)
                .width((screenWidth * 0.40f).dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            if (listAllDiarys.isNotEmpty()) {

                Text(
                    text = "Diaries",
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Start,
                    modifier = Modifier
                        .padding(top = 20.dp, start = 4.dp, bottom = 6.dp)
                        .fillMaxWidth()
                )

                LazyVerticalStaggeredGrid(
                    modifier = Modifier.weight(1f),
                    state = rememberLazyStaggeredGridState(),
                    columns = StaggeredGridCells.Fixed(2)
                ) {
                    items(listAllDiarys.size) { index ->
                        val diary = listAllDiarys[index]
                        val id = diary["diary_id"] ?: ""
                        val title = diary["title"] ?: "title"
                        val publisher = diary["publisher_username"] ?: "publisher"
                        val image = "${baseUrl}/${diary["thumbnail"]}"
                        Box(
                            modifier = Modifier
                                .padding(6.dp)
                                .background(Color.White)
                                .border(BorderStroke(1.dp, Color.Black))
                                .clickable {
                                    showLoadingDetails = true
                                    isSelectedDiary = true
                                    scope.launch {
                                        val response = retrofitClient.getDiaryDetailes(id)
                                        if (response.isSuccessful) {
                                            diarySelectedDetail = response.body()?.data
                                        } else {
                                            Toast.makeText(
                                                context,
                                                "Error in get details !!",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
                                    }
                                }
                        ) {
                            Column(
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                AsyncImage(
                                    model = image,
                                    contentDescription = null,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .padding(6.dp)
                                        .clip(RoundedCornerShape(13.dp))
                                        .sizeIn(maxHeight = 400.dp)
                                )

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = title,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.Black,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(start = 10.dp, bottom = 6.dp)
                                        .fillMaxWidth()
                                )

                                Text(
                                    text = publisher,
                                    fontSize = 16.sp,
                                    color = Color.Black,
                                    textAlign = TextAlign.Start,
                                    modifier = Modifier
                                        .padding(start = 10.dp, bottom = 6.dp)
                                        .fillMaxWidth()
                                )
                            }
                        }
                    }
                }

            } else {
                Text(
                    text = "Not Find Diaries",
                    color = Color.LightGray,
                    fontSize = 24.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )
            }

        }
        // show deteils
        if (isSelectedDiary) {
            if (showLoadingDetails && diarySelectedDetail == null) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    CircularProgressIndicator(color = Color.Black, modifier = Modifier.size(65.dp))
                    Spacer(modifier = Modifier.height(10.dp))
                    Text("Loading ...", color = Color.Black)
                }
            } else if (diarySelectedDetail != null) {
                val diarySelected = diarySelectedDetail
                val isFavoriteDiary =
                    listMyFavoritesDiarys.any { it["diary_id"] == diarySelected?.diary_id }
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
                ) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        AsyncImage(
                            model = "$baseUrl/${diarySelected?.thumbnail}",
                            contentDescription = null,
                            modifier = Modifier.fillMaxSize()
                        )
                        Icon(
                            painter = painterResource(R.drawable.icon_close),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.TopStart)
                                .clickable { diarySelectedDetail = null;isSelectedDiary = false }
                        )
                        Icon(
                            painter = painterResource(if (!isFavoriteDiary) R.drawable.icon_star_outline else R.drawable.icon_star_filled_gold),
                            contentDescription = null,
                            modifier = Modifier
                                .size(50.dp)
                                .align(Alignment.TopEnd)
                                .clickable {
                                    if (tokenSaved.isNotBlank()) {
                                        scope.launch {
                                            retrofitClient.addToFavorite(
                                                tokenSaved,
                                                InsertFavoriteRequest(diarySelected?.diary_id ?: "")
                                            )
                                        }
                                    } else {
                                        navController.navigate(Screen.ProfileScreen.route)
                                    }
                                }
                        )
                    }
                }
            } else {
                Text(
                    text = "Not Find !!",
                    color = Color.Red,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxSize()
                )
            }
        } else {
            Box(Modifier.fillMaxSize()) {
                Column(
                    modifier = Modifier
                        .align(Alignment.Center),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box {
                        Image(
                            painter = painterResource(R.drawable.art_icon_la_tour_eiffel),
                            contentDescription = null,
                            modifier = Modifier
                                .size(160.dp)
                                .alpha(0.4f)
                                .align(Alignment.TopEnd),
                            contentScale = ContentScale.Crop
                        )

                        Image(
                            painter = painterResource(R.drawable.art_icon_arc_de_triomphe),
                            contentDescription = null,
                            modifier = Modifier
                                .padding(end = 40.dp, top = 40.dp)
                                .size(160.dp),
                            contentScale = ContentScale.Crop
                        )
                    }
                    Text(
                        text = "Welcome to France",
                        fontSize = 26.sp,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.fillMaxWidth()
                    )
                }


            }
        }
    }

}