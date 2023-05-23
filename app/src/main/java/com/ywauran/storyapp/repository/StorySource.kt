package com.ywauran.storyapp.repository


import android.util.Log
import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.ywauran.storyapp.data.remote.api.ApiService
import com.ywauran.storyapp.data.remote.response.StoryResponse

class StorySource(private val apiService: ApiService, private val authorization: String): PagingSource<Int, StoryResponse>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override fun getRefreshKey(state: PagingState<Int, StoryResponse>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            val anchorPage = state.closestPageToPosition(anchorPosition)
            anchorPage?.prevKey?.plus(1) ?: anchorPage?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, StoryResponse> {
        return try {
            val page = params.key ?: INITIAL_PAGE_INDEX
            val responseData = apiService.getListStory(authorization, page, params.loadSize)

            val prefKey = if (page == INITIAL_PAGE_INDEX) null else page - 1
            val nextKey = if (responseData.listStory.isNullOrEmpty()) null else page + 1

            LoadResult.Page(
                data = responseData.listStory as List,
                prevKey = prefKey,
                nextKey = nextKey
            )
        }catch (e: Exception) {
            e.printStackTrace()
            Log.e(StorySource::class.java.simpleName, "Error getting data ${e.message}")
            LoadResult.Error(e)
        }
    }
}
