package com.mkarshnas6.karenstudio.myfrance.data.remote.model

import com.google.gson.annotations.SerializedName

data class SignInUserRequest(
    @SerializedName("userEmailAddress") val emailAddress: String,
    @SerializedName("userPassword") val password: String
)