package com.mkarshnas6.karenstudio.worldskill.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant

@Entity(
    tableName = AppConstant.DataBase.TABLE_CATEGORY
)
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val categoryId: Long = 0,
    val categoryName: String
)