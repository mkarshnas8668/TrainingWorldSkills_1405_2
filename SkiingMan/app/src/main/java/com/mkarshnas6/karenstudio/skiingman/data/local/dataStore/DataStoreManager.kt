package com.mkarshnas6.karenstudio.skiingman.data.local.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.mkarshnas6.karenstudio.skiingman.Utils.AppConstant
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(
    AppConstant.DataStore.DATA_STORE_BASE_NAME
)

class DataStoreManager(private val context: Context) {

    suspend fun saveString(key: String, value: String) {
        context.dataStore.edit { preferences ->
            preferences[stringPreferencesKey(key)] = value
        }
    }

    suspend fun readString(key: String, defaultValue: String): String {
        return context.dataStore.data.first()[stringPreferencesKey(key)] ?: defaultValue
    }

}