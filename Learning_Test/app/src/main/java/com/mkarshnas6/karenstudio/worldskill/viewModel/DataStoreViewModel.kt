package com.mkarshnas6.karenstudio.worldskill.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.mkarshnas6.karenstudio.worldskill.utils.DataStoreManager
import com.mkarshnas6.karenstudio.worldskill.utils.sharePref.PrefKeys
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DataStoreViewModel(application: Application) : AndroidViewModel(application) {

    private val dataStore = DataStoreManager(application)

    val name: StateFlow<String> = dataStore
        .readStringFlow(PrefKeys.NAME, "")
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = ""
        )

    val notif: StateFlow<Boolean> = dataStore
        .readBooleanFlow(PrefKeys.NOTIF, false)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.Eagerly,
            initialValue = false
        )

    // save
    fun saveName(name: String) {
        viewModelScope.launch {
            dataStore.saveString(PrefKeys.NAME, name)
        }
    }

    fun saveNotif(notif: Boolean) {
        viewModelScope.launch {
            dataStore.saveBoolean(PrefKeys.NOTIF, notif)
        }
    }

    fun deleteByKey(key: String) {
        viewModelScope.launch {
            dataStore.removeByKey(key)
        }
    }

    fun clearAll() {
        viewModelScope.launch {
            dataStore.clearAll()
        }
    }

}