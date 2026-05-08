package com.mkarshnas6.karenstudio.bodyfit.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import java.io.IOException

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(AppConstant.DataStore.BASE_NAME)

class DataStoreManger(private val context: Context) {

    // save data
    suspend fun saveString(key: String, value: String): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.dataStore.edit { preferences ->
                    preferences[stringPreferencesKey(key)] = value
                }
                true
            } catch (e: IOException) {
                false
            }
        }
    }

    suspend fun saveInt(key: String, value: Int): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.dataStore.edit { preferences ->
                    preferences[intPreferencesKey(key)] = value
                }
                true
            } catch (e: IOException) {
                false
            }
        }
    }

    suspend fun saveBoolean(key: String, value: Boolean): Boolean {
        return withContext(Dispatchers.IO) {
            try {
                context.dataStore.edit { preferences ->
                    preferences[booleanPreferencesKey(key)] = value
                }
                true
            } catch (e: IOException) {
                false
            }
        }
    }

    // read data
    suspend fun readString(key: String, defaultValue: String): String {
        return context.dataStore.data.first()[stringPreferencesKey(key)] ?: defaultValue
    }

    suspend fun readInt(key: String, defaultValue: Int): Int =
        context.dataStore.data.first()[intPreferencesKey(key)] ?: defaultValue

    suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean =
        context.dataStore.data.first()[booleanPreferencesKey(key)] ?: defaultValue

    // live data
    fun readStringFlow(key: String, defaultValue: String): Flow<String> =
        context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }

    fun readIntFlow(key: String, defaultValue: Int): Flow<Int> =
        context.dataStore.data.map { preferences ->
            preferences[intPreferencesKey(key)] ?: defaultValue
        }

    fun readBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> =
        context.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }

    // delete
    suspend fun deleteByKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(intPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
        }
    }

    // clear all
    suspend fun clearAll(){
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}
