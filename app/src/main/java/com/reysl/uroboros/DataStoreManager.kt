package com.reysl.uroboros

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.map

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("profile_data")

class DataStoreManager(val context: Context) {
    suspend fun saveProfileImage(imageUrl: String) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey("image_url")] = imageUrl
        }
    }

    suspend fun saveProfileName(username: String) {
        context.dataStore.edit { pref ->
            pref[stringPreferencesKey("username")] = username
        }
    }

    fun getProfileImage() = context.dataStore.data.map { pref ->
        pref[stringPreferencesKey("image_url")]
    }

    fun getProfileName() = context.dataStore.data.map { pref ->
        pref[stringPreferencesKey("username")]
    }
}