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
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.dicoding.submissionintermediate.R
import com.dicoding.submissionintermediate.ViewModelFactory
import com.dicoding.submissionintermediate.data.paging.LoadingStateAdapter
import com.dicoding.submissionintermediate.databinding.ActivityMainBinding
import com.dicoding.submissionintermediate.ui.login.LoginActivity
import com.dicoding.submissionintermediate.ui.maps.MapsActivity
import com.dicoding.submissionintermediate.ui.upload.UploadActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    private lateinit var adapter: StoriesAdapter

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

        mainViewModel.authToken.observe(this) { authToken ->
            if (!authToken.isNullOrEmpty()) {
                showStories(authToken)
            } else {
                val intent = Intent(this@MainActivity, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }


    }

    private fun showStories(token: String) {
        adapter = StoriesAdapter()
        binding.rvStory.layoutManager = LinearLayoutManager(this)

        adapter.addLoadStateListener { loadState ->
            val isLoading = loadState.refresh is LoadState.Loading
            binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE

        }

        binding.rvStory.adapter = adapter.withLoadStateFooter(
            footer = LoadingStateAdapter {
                adapter.retry()
            }
        )

        mainViewModel.getStory(token).observe(this){
            if(it != null) binding.progressBar.visibility = View.VISIBLE else binding.progressBar.visibility = View.GONE
            adapter.submitData(lifecycle, it)
        }
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
            R.id.action_map -> {
                startActivity(Intent(this, MapsActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}