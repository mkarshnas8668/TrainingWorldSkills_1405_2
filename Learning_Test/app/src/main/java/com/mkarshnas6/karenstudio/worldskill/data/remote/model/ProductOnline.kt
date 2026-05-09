package com.mkarshnas6.karenstudio.worldskill.data.remote.model

import com.google.gson.annotations.SerializedName

data class ProductOnline(
    val id: Int,
    val name: String,
    val description: String? = null,
    val price: Double,
    @SerializedName("discount_price") val discountPrice: Double? = null,
    val stock: Int,
    val sku: String? = null,
    @SerializedName("is_available") val isAvailable: Boolean,
    @SerializedName("category_id")val categoryId: Int,
    @SerializedName("created_at") val createDate: String,
)