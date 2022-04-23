package com.darkshandev.sutori.data.datasources

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.darkshandev.sutori.data.models.LoginResult
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

private val Context.dataStore by preferencesDataStore("session")

class SessionService @Inject constructor(@ApplicationContext appContext: Context) {
    private val dataStore = appContext.dataStore

    fun getUser(): Flow<LoginResult?> {
        return dataStore.data.map {
            if (it[TOKEN_KEY] != null) LoginResult(
                it[NAME_KEY] ?: "",
                it[NAME_KEY] ?: "",
                it[TOKEN_KEY] ?: "",
            ) else null

        }
    }

    suspend fun saveUser(user: LoginResult) {
        dataStore.edit { preferences ->
            preferences[NAME_KEY] = user.name
            preferences[UID_KEY] = user.userId
            preferences[TOKEN_KEY] = user.token
        }
    }

    suspend fun logout() {
        dataStore.edit {
            it.apply {
                remove(NAME_KEY)
                remove(TOKEN_KEY)
                remove(UID_KEY)
            }
        }
    }

    companion object {
        private val NAME_KEY = stringPreferencesKey("NAME")
        private val TOKEN_KEY = stringPreferencesKey("TOKEN")
        private val UID_KEY = stringPreferencesKey("UID")
    }
}