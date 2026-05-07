package com.mkarshnas6.karenstudio.worldskill.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit

class SharedPrefsManager(context: Context) {

    private val sharedPref: SharedPreferences =
        context.getSharedPreferences("DataWorldSkill", Context.MODE_PRIVATE)

    // save
    fun saveString(key: String, value: String) {
        sharedPref.edit { putString(key, value) }
    }

    fun saveInt(key: String, value: Int) {
        sharedPref.edit { putInt(key, value) }
    }

    fun saveBoolean(key: String, value: Boolean) {
        sharedPref.edit { putBoolean(key, value) }
    }

    // read
    fun getString(key: String, defaultValue: String = ""): String {
        return sharedPref.getString(key, defaultValue) ?: defaultValue
    }

    fun getInt(key: String, defaultValue: Int = 0): Int {
        return sharedPref.getInt(key, defaultValue)
    }

    fun getBoolean(key: String, defaultValue: Boolean = false): Boolean {
        return sharedPref.getBoolean(key, defaultValue)
    }

    // remove

    fun removeByKey(key: String) {
        sharedPref.edit { remove(key) }
    }

    fun clearAll() {
        sharedPref.edit { clear() }
    }

}
