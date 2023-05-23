package com.ywauran.storyapp.ui.story

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import androidx.paging.PagingSource
import androidx.paging.PagingState
import androidx.recyclerview.widget.ListUpdateCallback
import com.ywauran.storyapp.data.remote.response.DetailStoryResponse
import com.ywauran.storyapp.data.remote.response.HandlingResponse
import com.ywauran.storyapp.data.remote.response.ListStoryResponse
import com.ywauran.storyapp.data.remote.response.StoryResponse
import com.ywauran.storyapp.repository.StoryRepository
import com.ywauran.storyapp.ui.adapter.StoryAdapter
import com.ywauran.storyapp.helper.Result
import com.ywauran.storyapp.utils.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.runTest
import org.junit.Assert
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.Mockito.verify
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@ExperimentalCoroutinesApi
@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    @Mock
    private lateinit var storyRepository: StoryRepository
    private lateinit var storyViewModel: StoryViewModel
    @Mock
    private lateinit var file: File
    companion object {
        private const val desc = "desc"
        private const val EXISTING_ID_DETAIL_STORY = "ID"
        private const val NOT_FOUND_ID_DETAIL_STORY = "NOT FOUND"
    }


    @Before
    fun setUp() {
        storyViewModel = StoryViewModel(storyRepository)
    }

    @Test
    fun `when get stories with data not null then return list`() = runTest {
        val dummyData = generateSuccessDummyListStoryResponse()
        val pagingData = StoryPagingSource.snapshot(dummyData)

        val expectedLiveData = MutableLiveData<PagingData<StoryResponse>>()
        expectedLiveData.value = pagingData

        Mockito.`when`(storyRepository.getListStory()).thenReturn(expectedLiveData)

        val actualPagingData: PagingData<StoryResponse> = storyViewModel.getListStory().getOrAwaitValue()
        Mockito.verify(storyRepository).getListStory()

        val pagingDataDiffer = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noOpListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )
        pagingDataDiffer.submitData(actualPagingData)

        Assert.assertNotNull(pagingDataDiffer.snapshot())
        assertEquals(dummyData.size, pagingDataDiffer.snapshot().size)
        assertEquals(dummyData, pagingDataDiffer.snapshot())
        assertEquals(dummyData[0], pagingDataDiffer.snapshot()[0])
    }

    @Test
    fun `when get stories with data empty then return empty list`() = runTest {
        val dummyResponseWithData = generateSuccessDummyListStoryResponse()
        val dummyResponse = arrayListOf<StoryResponse>()
        val dataPaging = StoryPagingSource.snapshot(dummyResponse)

        val expectedValues = MutableLiveData<PagingData<StoryResponse>>()
        expectedValues.value = dataPaging

        Mockito.`when`(storyRepository.getListStory()).thenReturn(expectedValues)

        val actualValues = storyRepository.getListStory().getOrAwaitValue()
        Mockito.verify(storyRepository).getListStory()

        val differ = AsyncPagingDataDiffer(
            diffCallback = StoryAdapter.DIFF_CALLBACK,
            updateCallback = noOpListUpdateCallback,
            workerDispatcher = Dispatchers.Main
        )

        differ.submitData(actualValues)
        Assert.assertTrue(differ.snapshot().isEmpty())
        Assert.assertNotEquals(dummyResponseWithData, differ.snapshot())
    }

    @Test
    fun `when create story then return result success`() {
        val dataDummy = generateSuccessDummyCreateStory()
        val expectedValues = MutableLiveData<Result<HandlingResponse>>()
        expectedValues.value = Result.Success(dataDummy)

        Mockito.`when`(storyRepository.addStory(desc, file)).thenReturn(expectedValues)

        val actualValues = storyViewModel.addStory(desc, file).getOrAwaitValue()
        Mockito.verify(storyRepository).addStory(desc, file)

        Assert.assertNotNull(actualValues)
        Assert.assertTrue(actualValues is Result.Success)
        assertEquals((expectedValues.value as Result.Success).data, actualValues.data)
    }

    @Test
    fun `when create story then return result error`() {
        val expectedError = generateErrorDummyCreateStory()
        val expectedValues = MutableLiveData<Result<HandlingResponse>>().apply {
            value = Result.Error(data = expectedError, code = 400)
        }
        `when`(storyRepository.addStory(desc, file)).thenReturn(expectedValues)

        val actualResult = storyViewModel.addStory(desc, file).getOrAwaitValue()

        verify(storyRepository).addStory(desc, file)
        assertNotNull(actualResult)
        assertTrue(actualResult is Result.Error)
        assertEquals(expectedError.message, actualResult.data?.message)
    }

    @Test
    fun `when get list story with lat lon then return result success`() {
        val expectedData = generateSuccessDummyListStoryLocation()
        val expectedValue = Result.Success(expectedData)
        val expectedLiveData = MutableLiveData<Result<ListStoryResponse>>()
        expectedLiveData.value = expectedValue

        `when`(storyRepository.getListStoryLocation()).thenReturn(expectedLiveData)

        val actualLiveData = storyViewModel.getListStoryLocation()
        val actualValue = actualLiveData.getOrAwaitValue()

        verify(storyRepository).getListStoryLocation()

        assertNotNull(actualValue)
        assertTrue(actualValue is Result.Success)
        assertNotNull(actualValue.data?.listStory?.get(0)?.lat)
        assertNotNull(actualValue.data?.listStory?.get(0)?.lon)
        assertEquals(expectedValue.data?.listStory?.size, actualValue.data?.listStory?.size)
        assertEquals(expectedValue.data?.listStory?.get(0), actualValue.data?.listStory?.get(0))
    }

    @Test
    fun `when get detail story then return result success`() {
        val dataDummy = generateSuccessDummyDetailStory()
        val expectedValues = MutableLiveData<Result<DetailStoryResponse>>().apply {
            value = Result.Success(dataDummy)
        }
        Mockito.`when`(storyRepository.getDetailStory(EXISTING_ID_DETAIL_STORY)).thenReturn(expectedValues)

        val actualValues = storyViewModel.getDetailStory(EXISTING_ID_DETAIL_STORY).getOrAwaitValue()

        Mockito.verify(storyRepository).getDetailStory(EXISTING_ID_DETAIL_STORY)
        assertTrue(actualValues is Result.Success)
        assertEquals(expectedValues.value?.data?.story, actualValues.data?.story)
    }

    @Test
    fun `when get detail story then return result error`(){
        val dataDummy = generateErrorDummyDetailStory()
        val expectedValues = MutableLiveData<Result<DetailStoryResponse>>()
        expectedValues.value = Result.Error(data = dataDummy, code = 401)

        Mockito.`when`(storyRepository.getDetailStory(NOT_FOUND_ID_DETAIL_STORY)).thenReturn(expectedValues)
        val actualValues = storyViewModel.getDetailStory(NOT_FOUND_ID_DETAIL_STORY).getOrAwaitValue()
        Mockito.verify(storyRepository).getDetailStory(NOT_FOUND_ID_DETAIL_STORY)

        Assert.assertNotNull(actualValues)
        Assert.assertTrue(actualValues is Result.Error)
        assertEquals((expectedValues.value as Result.Error).data?.message, actualValues.data?.message)
    }

}


class StoryPagingSource : PagingSource<Int, List<StoryResponse>>() {

    companion object {
        fun snapshot(items: List<StoryResponse>): PagingData<StoryResponse> {
            return PagingData.from(items)
        }
    }

    override fun getRefreshKey(state: PagingState<Int, List<StoryResponse>>): Int? {
        return null
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, List<StoryResponse>> {
        return LoadResult.Page(emptyList(), null, null)
    }
}

val noOpListUpdateCallback = object : ListUpdateCallback {
    override fun onInserted(position: Int, count: Int) {
        // no-op
    }

    override fun onRemoved(position: Int, count: Int) {
        // no-op
    }

    override fun onMoved(fromPosition: Int, toPosition: Int) {
        // no-op
    }

    override fun onChanged(position: Int, count: Int, payload: Any?) {
        // no-op
    }
}
