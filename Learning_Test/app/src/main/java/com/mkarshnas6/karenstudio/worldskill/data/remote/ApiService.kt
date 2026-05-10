package com.mkarshnas6.karenstudio.worldskill.data.remote

import com.mkarshnas6.karenstudio.worldskill.data.remote.model.DeleteProductResponse
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.ProductOnline
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.RegisterUserRequest
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.RegisterUserResponse
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.TokenResponse
import com.mkarshnas6.karenstudio.worldskill.data.remote.model.UpdateProductRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {

    @POST("/register")
    suspend fun registerUser(@Body userInfo: RegisterUserRequest): Response<RegisterUserResponse>

    @POST("/token")
    @FormUrlEncoded
    suspend fun loginUser(
        @Field("username") username: String,
        @Field("password") password: String,
        @Field("grant_type") grantType: String = "password"
    ): Response<TokenResponse>

    @GET("/users/me")
    suspend fun getUserInfo(
        @Header("Authorization") token : String
    ): Response<RegisterUserResponse>

    @GET("/products")
    suspend fun getAllProducts(): Response<List<ProductOnline>>

    @PUT("/products/{id}")
    suspend fun updateProduct(
        @Path("id") productId: Int,
        @Body updatedProductRequest: UpdateProductRequest,
        @Header("Authorization") token: String
    ): Response<ProductOnline>

    @DELETE("/products/{id}")
    suspend fun deleteProduct(
        @Path("id") productId: Int,
        @Header("Authorization") token : String
    ): Response<DeleteProductResponse>

}