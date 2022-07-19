package com.ebenezer.gana.githubber.prefsStore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * Class responsible for storing and retrieving token to and from preferencesDataStore
 */

private val Context.dataStore by preferencesDataStore(("balance"))
class TokenPreferences(private val context: Context) {


    val tokenPrefs: Flow<String?>
        get() = context.dataStore.data.map { preferences ->
            preferences[TOKEN_KEY]
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[TOKEN_KEY] = token
        }
    }

    companion object {
        private val TOKEN_KEY = stringPreferencesKey("token_key")
    }
}