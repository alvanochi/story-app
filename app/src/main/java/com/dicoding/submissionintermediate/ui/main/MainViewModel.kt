package com.dicoding.submissionintermediate.ui.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.StoryRepository
import com.dicoding.submissionintermediate.data.response.ListStoryItem
import com.dicoding.submissionintermediate.helper.wrapEspressoIdlingResource
import kotlinx.coroutines.launch

class MainViewModel(private val authRepository: AuthRepository, private val storyRepository: StoryRepository) : ViewModel() {

    fun logout() {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                authRepository.logout()
            }
        }
    }


    val authToken: LiveData<String> = authRepository.getSession().asLiveData()


    fun getStory(token: String): LiveData<PagingData<ListStoryItem>> = storyRepository.getStories(token).cachedIn(viewModelScope)


}