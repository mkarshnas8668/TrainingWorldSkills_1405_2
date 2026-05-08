package com.mkarshnas6.karenstudio.worldskill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.OrderEntity
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertOrder(order: OrderEntity)

    @Query("SELECT * FROM ${AppConstant.DataBase.TABLE_ORDERS} ORDER BY orderDate DESC")
    fun getAllOrders(): Flow<List<OrderEntity>>

    @Query("SELECT * FROM ${AppConstant.DataBase.TABLE_ORDERS} WHERE productId = :productId ORDER BY orderDate DESC")
    fun getOrdersForProduct(productId: Long): Flow<List<OrderEntity>>

    @Delete
    suspend fun deleteOrder(order: OrderEntity)

    @Update
    suspend fun updateOrder(order: OrderEntity)

}