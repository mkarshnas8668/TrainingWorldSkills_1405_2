package com.mkarshnas6.karenstudio.worldskill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant

@Entity(tableName = AppConstant.DataBase.TABLE_PRODUCTS)
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val productId: Long = 0,
    val productName: String,
    val productPrice: Double,
    val productStock: Int = 0,
    val haveDigikala: Boolean = true,
    val inventory: Map<String, Int>
)