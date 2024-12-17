package com.dicoding.submissionintermediate.ui.upload

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.retrofit.ApiConfig
import com.dicoding.submissionintermediate.helper.wrapEspressoIdlingResource
import kotlinx.coroutines.launch
import okhttp3.MultipartBody
import okhttp3.RequestBody

class UploadViewModel(private val repository: AuthRepository): ViewModel() {

    private val _successUpload = MutableLiveData<String>()
    val successUpload: LiveData<String> = _successUpload

    private val _failUpload = MutableLiveData<String>()
    val failUpload: LiveData<String> = _failUpload

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val authToken: LiveData<String> = repository.getSession().asLiveData()

    fun uploadImage(
        multipartBody: MultipartBody.Part,
        description: RequestBody,
        lat: RequestBody? = null,
        lon: RequestBody? = null
    ) {
        wrapEspressoIdlingResource {
            viewModelScope.launch {
                _isLoading.value = true

                try {
                    val response = ApiConfig.getApiService()
                        .uploadImage("Bearer ${authToken.value!!}", multipartBody, description, lat, lon)
                    _isLoading.value = false
                    _successUpload.value = response.message!!

                } catch (e: Exception) {
                    Log.e("UPLOAD_ERROR", e.message ?: "Unknown error")
                    _isLoading.value = false
                    _failUpload.value = "Upload failed!"
                }
            }
        }
    }
}
