package com.ywauran.storyapp.ui.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.ywauran.storyapp.R
import com.ywauran.storyapp.data.remote.response.StoryResponse
import com.ywauran.storyapp.databinding.ActivityMainBinding
import com.ywauran.storyapp.ui.adapter.LoadingStateAdapter
import com.ywauran.storyapp.ui.adapter.StoryAdapter
import com.ywauran.storyapp.ui.auth.login.LoginActivity
import com.ywauran.storyapp.ui.maps.MapsStoryActivity
import com.ywauran.storyapp.ui.story.StoryViewModel
import com.ywauran.storyapp.ui.story.addstory.AddStoryActivity
import com.ywauran.storyapp.ui.story.detailstory.DetailStoryActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    private val storyViewModel: StoryViewModel by viewModels()

    @Inject
    lateinit var storyAdapter: StoryAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "List Story"
        initRecyclerView()

        initViews()
        initListeners()

        getData()
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                logout()
                return true
            }
        }
        return super.onOptionsItemSelected(item)
    }

    private fun initViews() {
        with(binding) {
            rvListStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun initListeners() {
        with(binding) {
            swipeRefresh.setOnRefreshListener {
                setLoading(true)
                storyAdapter.refresh()
            }

            fabAddStory.setOnClickListener {
                startActivity(Intent(this@MainActivity, AddStoryActivity::class.java))
            }

            fabMaps.setOnClickListener{
                startActivity(Intent(this@MainActivity, MapsStoryActivity::class.java))
            }

            storyAdapter.setOnItemClickListener(object : StoryAdapter.OnItemClickListener {
                override fun onItemClicked(id: String) {
                    val intent = Intent(this@MainActivity, DetailStoryActivity::class.java)
                    intent.putExtra(DetailStoryActivity.STORY_ID, id)
                    startActivity(intent)
                }
            })
        }
    }

    private fun getData() {
        setLoading(true)
        storyViewModel.getListStory().observe(this) { responseListStory ->
            storyAdapter.submitData(lifecycle, responseListStory)
            storyAdapter.addLoadStateListener { listener ->
                if (listener.refresh != LoadState.Loading) {
                    setLoading(false)
                }
                if (listener.refresh is LoadState.Error) {
                    val data = listener.refresh as LoadState.Error
                    handleLoadStateError(data.error)
                }
            }
        }
    }

    private fun handleLoadStateError(error: Throwable) {
        if (error.message.equals("HTTP 401 Unauthorized")) {
            handleUnauthorizedError()
        } else {
            binding.llError.isVisible = true
            binding.tvRetry.setOnClickListener {
                binding.llError.isVisible = false
                getData()
            }
            Log.e(MainActivity::class.java.simpleName, "Error activity ${error.message}")
            Log.e(MainActivity::class.java.simpleName, "Error activity localized ${error.localizedMessage}")
        }
    }

    private fun handleUnauthorizedError() {
        Toast.makeText(this@MainActivity, "Your session has timed out. Please log in again.", Toast.LENGTH_SHORT).show()
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        PreferenceManager.getDefaultSharedPreferences(this@MainActivity).edit().clear().apply()
        startActivity(intent)
        finish()
    }

    private fun initRecyclerView() {
        with(binding) {
            rvListStory.layoutManager = LinearLayoutManager(this@MainActivity)
            rvListStory.setHasFixedSize(true)
            rvListStory.adapter = storyAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter {
                    storyAdapter.retry()
                }
            )
        }
    }

    private fun logout() {
        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish()
    }



    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}