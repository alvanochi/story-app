package com.dicoding.submissionintermediate.ui.detail

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import com.dicoding.submissionintermediate.data.retrofit.response.Story
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class DetailViewModel(private val repository: AuthRepository): ViewModel() {
    private val _story = MutableLiveData<Story>()
    val story: LiveData<Story> = _story
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val authToken: LiveData<String> = repository.getSession().asLiveData()



    fun getStory(id: String){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiService().getDetailStory("Bearer ${authToken.value!!}", id)
                _isLoading.value = false

                _story.value = response.story!!

            }
            catch (e: SocketTimeoutException) {
                Log.e("API_ERROR", "Request timed out!")
            }
            catch (e: HttpException){
                _isLoading.value = false
                Log.e("catch", e.message.toString() )

            }
        }

    }
}