package com.mkarshnas6.karenstudio.worldskill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.ProductEntity
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertProduct(product: ProductEntity)

    @Query("SELECT * FROM ${AppConstant.DataBase.TABLE_PRODUCTS} ORDER BY productName ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM ${AppConstant.DataBase.TABLE_PRODUCTS} WHERE productId = :productId")
    suspend fun getProductById(productId: Long): ProductEntity?

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("SELECT * FROM ${AppConstant.DataBase.TABLE_PRODUCTS} WHERE productName LIKE '%' || :query || '%' OR productPrice LIKE '%' || :query || '%' OR productStock LIKE '%' || :query || '%' ")
    fun searchProducts(query: String): Flow<List<ProductEntity>>

    @Query("""SELECT * FROM products WHERE productPrice BETWEEN :minPrice AND :maxPrice""")
    fun searchByPriceRange(
        minPrice: Double,
        maxPrice: Double
    ): Flow<List<ProductEntity>>


}