package com.ywauran.storyapp.ui.story

import androidx.lifecycle.*
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.ywauran.storyapp.data.remote.response.DetailStoryResponse
import com.ywauran.storyapp.data.remote.response.HandlingResponse
import com.ywauran.storyapp.data.remote.response.ListStoryResponse
import com.ywauran.storyapp.data.remote.response.StoryResponse
import com.ywauran.storyapp.repository.StoryRepository
import com.ywauran.storyapp.helper.Result
import dagger.hilt.android.lifecycle.HiltViewModel
import java.io.File
import javax.inject.Inject

@HiltViewModel
class StoryViewModel @Inject constructor(private val storyRepository: StoryRepository) : ViewModel() {

    fun addStory(description: String, file: File): LiveData<Result<HandlingResponse>> {
        return storyRepository.addStory(description, file)
    }

    fun getListStory(): LiveData<PagingData<StoryResponse>> {
        return storyRepository.getListStory().cachedIn(viewModelScope)
    }

    fun getListStoryLocation(): LiveData<Result<ListStoryResponse>> {
        return storyRepository.getListStoryLocation()
    }

    fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>> {
        return storyRepository.getDetailStory(id)
    }
}