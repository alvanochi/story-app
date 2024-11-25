package com.dicoding.submissionintermediate.ui.detail

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.dicoding.submissionintermediate.ViewModelFactory
import com.dicoding.submissionintermediate.databinding.ActivityDetailBinding
import com.dicoding.submissionintermediate.ui.login.LoginActivity

class DetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDetailBinding
    private val detailViewModel by viewModels<DetailViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val idStory = intent.getStringExtra(ID_STORY)

        detailViewModel.isLoading.observe(this){
            showLoading(it)
        }


        detailViewModel.authToken.observe(this){
            if(it.isNullOrEmpty()) {
                val intent = Intent(this@DetailActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                detailViewModel.getStory(idStory!!)
                setupView()
            }
        }



    }

    private fun setupView(){
        detailViewModel.story.observe(this){
            Glide.with(binding.ivDetailPhoto.context)
                .load(it.photoUrl)
                .into(binding.ivDetailPhoto)
            binding.apply {
                tvDetailName.text = it.name
                tvDetailDescription.text = it.description
            }
        }
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return false
    }

    companion object {
        const val ID_STORY = "id"
    }
}