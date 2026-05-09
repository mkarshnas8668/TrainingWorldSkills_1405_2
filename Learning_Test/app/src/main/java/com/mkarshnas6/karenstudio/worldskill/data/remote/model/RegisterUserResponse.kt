package com.mkarshnas6.karenstudio.worldskill.data.remote.model

import com.google.gson.annotations.SerializedName

data class RegisterUserResponse(
    val id: String,
    val username: String,
    val email: String,
    val full_name: String?,
    val phone: String?,
    val address: String?,
    val role: String,
    @SerializedName("is_active") val isActive: Boolean,
    val created_at: String,
)
