package com.mkarshnas6.karenstudio.worldskill.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverter
import com.mkarshnas6.karenstudio.worldskill.data.local.dao.CategoryDao
import com.mkarshnas6.karenstudio.worldskill.data.local.dao.OrderDao
import com.mkarshnas6.karenstudio.worldskill.data.local.dao.ProductDao
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.CategoryEntity
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.OrderEntity
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.ProductEntity
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant

@Database(
    entities = [
        ProductEntity::class,
        OrderEntity::class,
        CategoryEntity::class
    ],
    version = 2,
    exportSchema = false
)
//@TypeConverter(DB_TypeConverter::class) || @TypeConverter(DB_TypeConverter::class , DateTypeConverter::class , ListTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun categoryDao(): CategoryDao


    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AppConstant.DataBase.DB_NAME
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}