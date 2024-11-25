package com.dicoding.submissionintermediate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.pref.UserPreference
import com.dicoding.submissionintermediate.data.pref.dataStore
import com.dicoding.submissionintermediate.di.Injection
import com.dicoding.submissionintermediate.ui.detail.DetailViewModel
import com.dicoding.submissionintermediate.ui.login.LoginViewModel
import com.dicoding.submissionintermediate.ui.main.MainViewModel
import com.dicoding.submissionintermediate.ui.upload.UploadViewModel

class ViewModelFactory(private val repository: AuthRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(repository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(repository) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null

        @JvmStatic
        fun getInstance(context: Context): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(Injection.provideAuthRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

}
