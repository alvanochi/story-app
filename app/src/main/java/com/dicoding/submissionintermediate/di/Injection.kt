package com.dicoding.submissionintermediate.di

import android.content.Context
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.StoryRepository
import com.dicoding.submissionintermediate.data.pref.UserPreference
import com.dicoding.submissionintermediate.data.pref.dataStore
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import com.dicoding.submissionintermediate.data.room.StoriesDatabase

object Injection {
    fun provideAuthRepository(context: Context): AuthRepository {
        val pref = UserPreference.getInstance(context.dataStore)
        return AuthRepository.getInstance(pref)
    }

    fun provideStoryRepository(context: Context): StoryRepository {
        val apiService = ApiConfig.getApiService()
        val storiesDatabase = StoriesDatabase.getDatabase(context)
        return StoryRepository.getInstance(apiService, storiesDatabase)
    }
}