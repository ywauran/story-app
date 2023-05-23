package com.ywauran.storyapp.ui.story.detailstory

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.preference.PreferenceManager
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import com.bumptech.glide.Glide
import com.ywauran.storyapp.databinding.ActivityDetailStoryBinding
import com.ywauran.storyapp.helper.changeFormatDate
import com.ywauran.storyapp.ui.auth.login.LoginActivity
import com.ywauran.storyapp.ui.story.StoryViewModel
import com.ywauran.storyapp.helper.Result
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DetailStoryActivity : AppCompatActivity() {

    companion object {
        const val STORY_ID = "STORY_ID"
    }

    private lateinit var binding: ActivityDetailStoryBinding
    private val storyViewModel: StoryViewModel by viewModels()
    private var id: String = "0"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailStoryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = "Detail Story"

        id = intent.getStringExtra(STORY_ID).toString()
        setLoading(true)
        storyViewModel.getDetailStory(id)
        setDetailStory()
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    private fun setDetailStory() {
        storyViewModel.getDetailStory(id).observe(this) { detailStoryResponse ->
            when (detailStoryResponse){
                is Result.Loading -> setLoading(true)
                is Result.Success -> {
                    setLoading(false)
                    with(binding) {
                        Glide.with(this@DetailStoryActivity)
                            .load(detailStoryResponse.data?.story?.photoUrl)
                            .into(ivStory)
                        tvDate.text = "${changeFormatDate(detailStoryResponse.data?.story?.createdAt as String)}"
                        tvTitle.text = detailStoryResponse.data?.story?.name
                        tvDescription.text = detailStoryResponse.data?.story?.description
                    }
                }
                else -> {
                    setLoading(false)
                    if (detailStoryResponse.code == 401) {
                        PreferenceManager.getDefaultSharedPreferences(this).edit().clear().apply()
                        val intent = Intent(this, LoginActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    }
                    Toast.makeText(this@DetailStoryActivity, detailStoryResponse.message, Toast.LENGTH_SHORT).show()
                    finish()
                }
        }

        }
    }

    private fun setLoading(isLoading: Boolean) {
        binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
    }
}
