package com.felipeserver.site.glyphnotes.ui.viewmodel.ui

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore by preferencesDataStore("settings")

object PreferenceKeys {
    val USER_NAME = stringPreferencesKey("user_name")
    val IS_MATERIALYOU_THEME = booleanPreferencesKey("is_material_you_theme")
}

suspend fun saveUsername(context: Context, name: String) {
    context.dataStore.edit { settings ->
        settings[PreferenceKeys.USER_NAME] = name }
}

fun getUsername(context: Context): Flow<String?> {
    return context.dataStore.data.map { preferences ->
        preferences[PreferenceKeys.USER_NAME] }
}

