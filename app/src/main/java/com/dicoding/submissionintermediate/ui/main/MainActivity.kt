package com.dicoding.submissionintermediate.ui.main

import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionintermediate.R
import com.dicoding.submissionintermediate.ViewModelFactory
import com.dicoding.submissionintermediate.data.retrofit.response.ListStoryItem
import com.dicoding.submissionintermediate.databinding.ActivityMainBinding
import com.dicoding.submissionintermediate.ui.login.LoginActivity
import com.dicoding.submissionintermediate.ui.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val mainViewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val toolbar: Toolbar = binding.toolbar
        setSupportActionBar(toolbar)


        binding.btnAddStory.setOnClickListener {
            val intent = Intent(this@MainActivity, UploadActivity::class.java)
            startActivity(intent)
        }

        mainViewModel.isLoading.observe(this){
            showLoading(it)
        }

        mainViewModel.authToken.observe(this) { authToken ->
            if (!authToken.isNullOrEmpty()) {
                mainViewModel.getStories()
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        mainViewModel.stories.observe(this){
            showStories(it)
        }


    }

    private fun showStories(stories: List<ListStoryItem>) {
        if (stories.isNotEmpty()) {
            val adapter = StoriesAdapter()
            adapter.submitList(stories)
            binding.rvStory.adapter = adapter
            binding.rvStory.layoutManager = LinearLayoutManager(this)
        }
    }

    private fun showLoading(isLoading: Boolean){
        if(isLoading) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_nav, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_logout -> {
                mainViewModel.logout()
                true
            }
            R.id.action_setting -> {
                startActivity(Intent(Settings.ACTION_LOCALE_SETTINGS))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onResume(){
        super.onResume()
        mainViewModel.authToken.observe(this) { authToken ->
            if (!authToken.isNullOrEmpty()) {
                mainViewModel.getStories()
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }
}