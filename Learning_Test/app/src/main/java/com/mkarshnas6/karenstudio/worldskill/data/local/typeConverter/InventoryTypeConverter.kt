package com.mkarshnas6.karenstudio.worldskill.data.local.typeConverter

import androidx.core.view.WindowInsetsCompat
import androidx.room.TypeConverter
import androidx.room.TypeConverters
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class InventoryTypeConverter {

    @TypeConverter
    fun inventoryMapToString(inventory: Map<String, Int>): String {
        return Gson().toJson(inventory)
    }

    @TypeConverter
    fun stringToInventoryMap(stringInventory: String): Map<String, Int> {
        val type = object : TypeToken<Map<String, Int>>() {}.type
        return Gson().fromJson(stringInventory, type)
    }

}