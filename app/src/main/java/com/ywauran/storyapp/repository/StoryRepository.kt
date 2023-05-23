package com.ywauran.storyapp.repository


import android.content.Context
import android.preference.PreferenceManager
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.ywauran.storyapp.helper.Result
import com.ywauran.storyapp.data.remote.api.ApiService
import com.ywauran.storyapp.data.remote.response.DetailStoryResponse
import com.ywauran.storyapp.data.remote.response.HandlingResponse
import com.ywauran.storyapp.data.remote.response.ListStoryResponse
import com.ywauran.storyapp.data.remote.response.StoryResponse
import com.ywauran.storyapp.helper.convertErrorData
import com.ywauran.storyapp.helper.prepareFilePart
import dagger.hilt.android.qualifiers.ApplicationContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import javax.inject.Inject

class StoryRepository @Inject constructor(
    private val apiService: ApiService,
    @ApplicationContext private val context: Context
) {
    private val authorization = "Bearer ${
        PreferenceManager.getDefaultSharedPreferences(context).getString("PREF_TOKEN", "")
    }";

    fun getListStory(): LiveData<PagingData<StoryResponse>> =
        Pager(
            config = PagingConfig(pageSize = 5),
            pagingSourceFactory = { StorySource(apiService, authorization) }
        ).liveData

    fun getListStoryLocation(): LiveData<Result<ListStoryResponse>> {
        val data = MutableLiveData<Result<ListStoryResponse>>()
        data.value = Result.Loading()

        try {
            apiService.getListStory(authorization, 1).enqueue(object : Callback<ListStoryResponse> {
                override fun onResponse(call: Call<ListStoryResponse>, response: Response<ListStoryResponse>) {
                    if (response.isSuccessful) {
                        data.value = Result.Success(response.body()!!)
                    } else {
                        val errorData = response.errorBody()?.string()?.let { convertErrorData(it) }
                        data.value = Result.Error(errorData?.message ?: "Error getting data", response.code(), null)
                    }
                }

                override fun onFailure(call: Call<ListStoryResponse>, t: Throwable) {
                    data.value = Result.Error(t.message ?: "Error getting data", null, null)
                }
            })
        } catch (e: Exception) {
            data.value = Result.Error("Error converting data", null, null)
            e.printStackTrace()
        }

        return data
    }


    fun getDetailStory(id: String): LiveData<Result<DetailStoryResponse>> {
        val data = MutableLiveData<Result<DetailStoryResponse>>()
        try {
            apiService.getDetailStory(authorization, id).enqueue(object : Callback<DetailStoryResponse> {
                override fun onResponse(call: Call<DetailStoryResponse>, response: Response<DetailStoryResponse>) {
                    if (response.isSuccessful) {
                        data.postValue(Result.Success(response.body() as DetailStoryResponse))
                    } else {
                        val errorData = response.errorBody()?.string()?.let { convertErrorData(it) }
                        data.postValue(Result.Error(errorData?.message ?: "Error getting data", response.code(), null))
                    }
                }

                override fun onFailure(call: Call<DetailStoryResponse>, t: Throwable) {
                    data.postValue(Result.Error(t.message.toString(), null, null))
                }
            })
        } catch (e: Exception) {
            e.printStackTrace()
            data.postValue(Result.Error("Error converting data", null, null))
        }
        return data
    }


    fun addStory(description: String, file: File): LiveData<Result<HandlingResponse>> {
        val data = MutableLiveData<Result<HandlingResponse>>()
        try {
            val descriptionRequestBody = description.toRequestBody("text/plain".toMediaType())
            val storyFile = prepareFilePart("photo", file)
            apiService.addStory(authorization, descriptionRequestBody, storyFile)
                .enqueue(object : Callback<HandlingResponse> {
                    override fun onResponse(
                        call: Call<HandlingResponse>,
                        response: Response<HandlingResponse>
                    ) {
                        if (response.isSuccessful) {
                            data.postValue(Result.Success(response.body() as HandlingResponse))
                        } else {
                            val errorData = response.errorBody()?.string()?.let { convertErrorData(it) }
                            data.postValue(
                                Result.Error(
                                    errorData?.message ?: "Error adding story",
                                    response.code(),
                                    null
                                )
                            )
                        }
                    }

                    override fun onFailure(call: Call<HandlingResponse>, t: Throwable) {
                        data.postValue(Result.Error(t.message.toString(), null, null))
                    }
                })
        } catch (e: Exception) {
            e.printStackTrace()
            data.postValue(Result.Error("Error adding story", null, null))
        }
        return data
    }
}

