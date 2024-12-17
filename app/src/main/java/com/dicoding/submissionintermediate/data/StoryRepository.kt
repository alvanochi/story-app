package com.dicoding.submissionintermediate.data

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.submissionintermediate.data.paging.StoriesRemoteMediator
import com.dicoding.submissionintermediate.data.response.ListStoryItem
import com.dicoding.submissionintermediate.data.retrofit.ApiService
import com.dicoding.submissionintermediate.data.room.StoriesDatabase

class StoryRepository private constructor(private val apiService: ApiService, private val storiesDatabase: StoriesDatabase) {
    @OptIn(ExperimentalPagingApi::class)
    fun getStories(token: String): LiveData<PagingData<ListStoryItem>> {
        return Pager(
            config = PagingConfig(
                pageSize = 5
            ),
            remoteMediator = StoriesRemoteMediator(storiesDatabase, apiService, token),
            pagingSourceFactory = {
                storiesDatabase.storiesDao().getStories()
            }
        ).liveData
    }

    companion object {
        @Volatile
        private var instance: StoryRepository? = null
        fun getInstance(
            apiService: ApiService,
            storiesDatabase: StoriesDatabase
        ): StoryRepository =
            instance ?: synchronized(this) {
                instance ?: StoryRepository(apiService, storiesDatabase)
            }.also { instance = it }
    }
}