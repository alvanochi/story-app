package com.dicoding.submissionintermediate.ui.maps

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.response.ListStoryItem
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import kotlinx.coroutines.launch
import retrofit2.HttpException

class MapsViewModel(private val repository: AuthRepository): ViewModel() {

    private val _storiesWithLocation = MutableLiveData<List<ListStoryItem>>()
    val storiesWithLocation: LiveData<List<ListStoryItem>> = _storiesWithLocation
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading


    val authToken: LiveData<String> = repository.getSession().asLiveData()


    fun getStoriesWithLocation(){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiService().getStoriesWithLocation("Bearer ${authToken.value!!}")
                _isLoading.value = false

                _storiesWithLocation.value = response.listStory

            }
            catch (e: HttpException){
                _isLoading.value = false
                Log.e("getStoriesWithLocation", e.message.toString())
            }
        }

    }
}