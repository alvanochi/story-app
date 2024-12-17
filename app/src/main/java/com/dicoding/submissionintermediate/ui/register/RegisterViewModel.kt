package com.dicoding.submissionintermediate.ui.register

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionintermediate.data.response.DataResponse
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.launch
import retrofit2.HttpException
import java.net.SocketTimeoutException

class RegisterViewModel: ViewModel() {

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading
    private val _responseRegister = MutableLiveData<String>()
    val responseRegister: LiveData<String> = _responseRegister
    private val _isSuccess = MutableLiveData<DataResponse>()
    val isSuccess: LiveData<DataResponse> = _isSuccess

     fun registerUser(name: String, email: String, password: String){
         viewModelScope.launch {
             _isLoading.value = true
             try {
                 val response = ApiConfig.getApiService().register(name,email, password)
                 _isLoading.value = false
                 _isSuccess.value = response

            }
             catch (e: SocketTimeoutException) {
                 Log.e("API_ERROR", "Request timed out!")
                 _responseRegister.value = "Request timed out!"
             }
             catch (e: HttpException){
                 _isLoading.value = false
                 val jsonInString = e.response()?.errorBody()?.string()
                 val errorBody = Gson().fromJson(jsonInString, DataResponse::class.java)
                 _responseRegister.value = errorBody.message!!
            }
        }

    }
}