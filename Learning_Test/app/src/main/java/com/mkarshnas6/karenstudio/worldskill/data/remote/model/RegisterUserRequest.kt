package com.mkarshnas6.karenstudio.worldskill.data.remote.model

import com.google.gson.annotations.SerializedName

data class RegisterUserRequest(
    @SerializedName("username")
    val username: String,
    @SerializedName("email")
    val email: String,
    @SerializedName("password")
    val password: String,
    @SerializedName("full_name")
    val full_name: String? = null,
    @SerializedName("phone")
    val phone: String? = null,
    @SerializedName("address")
    val address: String? = null
)