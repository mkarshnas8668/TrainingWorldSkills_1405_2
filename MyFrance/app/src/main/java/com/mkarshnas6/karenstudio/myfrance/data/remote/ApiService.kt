package com.mkarshnas6.karenstudio.myfrance.data.remote

import com.mkarshnas6.karenstudio.myfrance.data.remote.model.AllDiariesResponse
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.DiaryDetailResponse
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.GetMyFavoriteResponse
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.InsertFavoriteRequest
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.InsertFavoriteResponse
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.SignInUserRequest
import com.mkarshnas6.karenstudio.myfrance.data.remote.model.SignInUserResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/api/users/signin")
    suspend fun signInUser(@Body user: SignInUserRequest): Response<SignInUserResponse>

    @GET("/api/diary/list")
    suspend fun getAllDiary(): Response<AllDiariesResponse>

    @GET("/api/diary/{diary_id}")
    suspend fun getDiaryDetailes(
        @Path("diary_id") diaryId: String
    ): Response<DiaryDetailResponse>

    @PUT("/api/diary/collection")
    suspend fun addToFavorite(
        @Header("auth_token") authToken: String,
        @Body() diary_id: InsertFavoriteRequest
    ): Response<InsertFavoriteResponse>

    @GET("/api/diary/collection")
    suspend fun getMyFavorites(
        @Header("auth_token") authToken: String
    ): Response<GetMyFavoriteResponse>

}