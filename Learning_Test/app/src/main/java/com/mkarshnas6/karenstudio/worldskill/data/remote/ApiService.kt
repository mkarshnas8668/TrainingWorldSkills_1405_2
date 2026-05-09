package com.mkarshnas6.karenstudio.worldskill.data.remote

import com.mkarshnas6.karenstudio.worldskill.data.remote.model.ProductOnline
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.RegisterUserRequest
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.RegisterUserResponse
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.TokenResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.POST

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body userInfo: RegisterUserRequest): Response<RegisterUserResponse>

    @POST("/token")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_typ") grantType: String = "password"
    ): Response<TokenResponse>

    @GET("/products")
    suspend fun getAllProducts(): Response<List<ProductOnline>>


}