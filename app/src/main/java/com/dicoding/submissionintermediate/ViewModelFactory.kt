package com.dicoding.submissionintermediate

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.submissionintermediate.data.AuthRepository
import com.dicoding.submissionintermediate.data.StoryRepository
import com.dicoding.submissionintermediate.di.Injection
import com.dicoding.submissionintermediate.ui.detail.DetailViewModel
import com.dicoding.submissionintermediate.ui.login.LoginViewModel
import com.dicoding.submissionintermediate.ui.main.MainViewModel
import com.dicoding.submissionintermediate.ui.maps.MapsViewModel
import com.dicoding.submissionintermediate.ui.upload.UploadViewModel

class ViewModelFactory(private val authRepository: AuthRepository, private val storyRepository: StoryRepository) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(authRepository, storyRepository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(DetailViewModel::class.java) -> {
                DetailViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(UploadViewModel::class.java) -> {
                UploadViewModel(authRepository) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel(authRepository) as T
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
                    INSTANCE = ViewModelFactory(Injection.provideAuthRepository(context), Injection.provideStoryRepository(context))
                }
            }
            return INSTANCE as ViewModelFactory
        }
    }

}
