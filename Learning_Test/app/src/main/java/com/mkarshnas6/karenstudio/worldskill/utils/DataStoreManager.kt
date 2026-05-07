package com.mkarshnas6.karenstudio.worldskill.utils

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mkarshnas6.karenstudio.worldskill.utils.sharePref.DataStoreKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = DataStoreKey.NAME_BASE)

class DataStoreManager(private val context: Context) {

    suspend fun saveString(key: String, value: String = "") {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun saveBoolean(key: String, value: Boolean = false) {
        context.dataStore.edit { preferences ->
            preferences[booleanPreferencesKey(key)] = value
        }
    }

    // read live
    fun readStringFlow(key: String, defaultValue: String): Flow<String> {
        return context.dataStore.data.map { preferences ->
            preferences[stringPreferencesKey(key)] ?: defaultValue
        }
    }

    fun readBooleanFlow(key: String, defaultValue: Boolean): Flow<Boolean> {
        return context.dataStore.data.map { preferences ->
            preferences[booleanPreferencesKey(key)] ?: defaultValue
        }
    }

    // read
    suspend fun readString(key: String, defaultValue: String): String {
        return context.dataStore.data.first()[stringPreferencesKey(key)] ?: defaultValue
    }

    suspend fun readBoolean(key: String, defaultValue: Boolean): Boolean {
        return context.dataStore.data.first()[booleanPreferencesKey(key)] ?: defaultValue
    }

    // clear
    suspend fun removeByKey(key: String) {
        context.dataStore.edit { preferences ->
            preferences.remove(stringPreferencesKey(key))
            preferences.remove(booleanPreferencesKey(key))
        }
    }

    suspend fun clearAll() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

}