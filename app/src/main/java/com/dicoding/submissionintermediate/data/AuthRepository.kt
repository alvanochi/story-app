package com.dicoding.submissionintermediate.data

import com.dicoding.submissionintermediate.data.pref.UserPreference
import kotlinx.coroutines.flow.Flow

class AuthRepository private constructor(
    private val userPreference: UserPreference
) {

    suspend fun saveSession(token: String) {
        userPreference.saveSession(token)
    }

    fun getSession(): Flow<String> {
        return userPreference.getSession()
    }

    suspend fun logout() {
        userPreference.logout()
    }

    companion object {
        @Volatile
        private var instance: AuthRepository? = null
        fun getInstance(
            userPreference: UserPreference
        ): AuthRepository =
            instance ?: synchronized(this) {
                instance ?: AuthRepository(userPreference)
            }.also { instance = it }
    }
}