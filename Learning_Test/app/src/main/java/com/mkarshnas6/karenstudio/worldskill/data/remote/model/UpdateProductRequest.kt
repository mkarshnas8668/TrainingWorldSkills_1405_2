package com.mkarshnas6.karenstudio.worldskill.data.remote.model

import com.google.gson.annotations.SerializedName

data class UpdateProductRequest(
    @SerializedName("name")
    val name: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("price")
    val price: Double? = null,

    @SerializedName("discount_price")
    val discountPrice: Double? = null,

    @SerializedName("stock")
    val stock: Int? = null,

    @SerializedName("sku")
    val sku: String? = null,

    @SerializedName("category_id")
    val categoryId: Int? = null,

    @SerializedName("is_available")
    val isAvailable: Boolean? = null
)