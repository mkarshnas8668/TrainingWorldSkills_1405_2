package com.mkarshnas6.karenstudio.worldskill.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant

@Entity(
    tableName = AppConstant.DataBase.TABLE_ORDERS,
    foreignKeys = [
        ForeignKey(
            entity = ProductEntity::class,
            parentColumns = ["productId"],
            childColumns = ["productId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("productId")]
)
data class OrderEntity(
    @PrimaryKey(autoGenerate = true)
    val orderId: Long = 0,
    val productId: Long,
    val customerName: String,
    val quantity: Int,
    val orderDate: Long = System.currentTimeMillis(),
    val totalPrice: Double
)