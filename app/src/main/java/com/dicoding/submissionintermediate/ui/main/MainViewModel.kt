package com.dicoding.submissionintermediate.ui.main

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import com.dicoding.submissionintermediate.data.retrofit.response.ListStoryItem
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class MainViewModel(private val repository: AuthRepository) : ViewModel() {

    private val _stories = MutableLiveData<List<ListStoryItem>>()
    val stories: LiveData<List<ListStoryItem>> = _stories
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    fun logout() {
        viewModelScope.launch {
            repository.logout()
        }
    }

    val authToken: LiveData<String> = repository.getSession().asLiveData()


    fun getStories(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiService().getStories("Bearer ${authToken.value!!}")
                _isLoading.value = false

                _stories.value = response.listStory

            }
            catch (e: SocketTimeoutException) {
                Log.e("API_ERROR", "Request timed out!")
            }
            catch (e: HttpException){
                _isLoading.value = false
                Log.e("getStories", e.message.toString())
            }
        }

    }


}