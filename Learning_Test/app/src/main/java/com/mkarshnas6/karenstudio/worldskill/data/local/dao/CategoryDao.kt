package com.mkarshnas6.karenstudio.worldskill.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import androidx.room.Update
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.CategoryEntity
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant
import kotlinx.coroutines.flow.Flow

@Dao
interface CategoryDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity)

    @Query("SELECT * FROM ${AppConstant.DataBase.TABLE_CATEGORY} ORDER BY categoryName ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Update
    suspend fun updateCategory(category: CategoryEntity)
}