package com.mkarshnas6.karenstudio.worldskill.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.mkarshnas6.karenstudio.worldskill.data.local.dao.CategoryDao
import com.mkarshnas6.karenstudio.worldskill.data.local.dao.OrderDao
import com.mkarshnas6.karenstudio.worldskill.data.local.dao.ProductDao
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.CategoryEntity
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.OrderEntity
import com.mkarshnas6.karenstudio.worldskill.data.local.entity.ProductEntity
import com.mkarshnas6.karenstudio.worldskill.data.local.typeConverter.InventoryTypeConverter
import com.mkarshnas6.karenstudio.worldskill.utils.AppConstant

@Database(
    entities = [
        ProductEntity::class,
        OrderEntity::class,
        CategoryEntity::class
    ],
    version = 3,
    exportSchema = false
)
//@TypeConverter(DB_TypeConverter::class) || @TypeConverter(DB_TypeConverter::class , DateTypeConverter::class , ListTypeConverter::class)
@TypeConverters(InventoryTypeConverter::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun orderDao(): OrderDao
    abstract fun categoryDao(): CategoryDao


    companion object {

        val MIGRATION_2_3 = object : Migration(2, 3) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    """
                    ALTER TABLE ${AppConstant.DataBase.TABLE_PRODUCTS}
                    ADD COLUMN haveDigikala INTEGER NOT NULL DEFAULT 1
                """.trimIndent()
                )
            }
        }

        @Volatile
        private var INSTANCE: AppDatabase? = null
        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    AppConstant.DataBase.DB_NAME
                )
                    .addMigrations(MIGRATION_2_3)
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }

}