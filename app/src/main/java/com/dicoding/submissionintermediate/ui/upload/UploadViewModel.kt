package com.dicoding.submissionintermediate.ui.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import com.dicoding.submissionintermediate.data.retrofit.response.DataResponse
import com.google.gson.Gson
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.HttpException
import java.net.SocketTimeoutException

class UploadViewModel(private val repository: AuthRepository): ViewModel() {

    private val _responseUpload = MutableLiveData<String>()
    val responseUpload: LiveData<String> = _responseUpload
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val authToken: LiveData<String> = repository.getSession().asLiveData()


    fun uploadImage(multipartBody: MultipartBody.Part, requestBody: RequestBody){
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val response = ApiConfig.getApiService()
                    .uploadImage("Bearer ${authToken.value!!}", multipartBody, requestBody)
                _isLoading.value = false

                _responseUpload.value = response.message!!

            }
            catch (e: SocketTimeoutException) {
                Log.e("API_ERROR", "Request timed out!")
                _responseUpload.value = "Request timed out!"
            }
            catch (e: HttpException) {
                _isLoading.value = false
                val jsonInString = e.response()?.errorBody()?.string()
                val errorBody = Gson().fromJson(jsonInString, DataResponse::class.java)
                _responseUpload.value = errorBody.message!!

            }
        }

    }


}