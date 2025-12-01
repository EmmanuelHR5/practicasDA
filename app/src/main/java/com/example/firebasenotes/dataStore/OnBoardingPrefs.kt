package com.example.firebasenotes.dataStore


import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("onboarding_prefs")
private val ONBOARDING_SEEN = booleanPreferencesKey("onboarding_seen")

fun getOnboardingSeen(context: Context): Flow<Boolean> =
    context.dataStore.data.map { prefs ->
        prefs[ONBOARDING_SEEN] ?: false
    }

suspend fun setOnboardingSeen(context: Context) {
    context.dataStore.edit { prefs ->
        prefs[ONBOARDING_SEEN] = true
    }
}
