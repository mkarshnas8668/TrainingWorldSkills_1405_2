package com.mkarshnas6.karenstudio.worldskill.data.remote.model

import com.google.gson.annotations.SerializedName

data class TokenResponse(
    @SerializedName("access_token") val accessToken : String,
    @SerializedName("token_type") val tokenType : String
)
