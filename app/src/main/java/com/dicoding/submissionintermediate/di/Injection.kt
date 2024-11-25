package com.dicoding.submissionintermediate.di

import android.content.Context
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.pref.UserPreference
import com.dicoding.submissionintermediate.data.pref.dataStore

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return AuthRepository.getInstance(pref)
    }
}