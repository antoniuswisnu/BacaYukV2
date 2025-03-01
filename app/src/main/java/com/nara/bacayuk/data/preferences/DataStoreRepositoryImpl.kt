package com.nara.bacayuk.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.nara.bacayuk.data.model.User
import com.nara.bacayuk.utils.EMAIL
import com.nara.bacayuk.utils.FULL_NAME_USER
import com.nara.bacayuk.utils.PREFERENCES_NAME
import com.nara.bacayuk.utils.UID
import kotlinx.coroutines.flow.first

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = PREFERENCES_NAME)

class DataStoreRepositoryImpl (private val context: Context): DataStoreRepository {

    override suspend fun putString(key: String, value: String) {
        val preferencesKey = stringPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun putInt(key: String, value: Int) {
        val preferencesKey = intPreferencesKey(key)
        context.dataStore.edit { preferences ->
            preferences[preferencesKey] = value
        }
    }

    override suspend fun getString(key: String): String? {
        return try {
            val preferencesKey = stringPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[preferencesKey]
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun getInt(key: String): Int? {
        return try {
            val preferencesKey = intPreferencesKey(key)
            val preferences = context.dataStore.data.first()
            preferences[preferencesKey]
        }catch (e: Exception){
            e.printStackTrace()
            null
        }
    }

    override suspend fun clear() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }

    override suspend fun putUser(key: String, user: User) {
        user.apply {
            uuid?.let { putString(UID, it) }
            fullName?.let { putString(FULL_NAME_USER, it) }
            email?.let { putString(EMAIL, it) }
        }
    }

    override suspend fun getUser(): User {
        val uid = getString(UID)
        val fullName = getString(FULL_NAME_USER)
        val email = getString(EMAIL)
        return User(uid, fullName, email)
    }

    override fun getDataStore(): DataStore<*> {
        return context.dataStore
    }
}