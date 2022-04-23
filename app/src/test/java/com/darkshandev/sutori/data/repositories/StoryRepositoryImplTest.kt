package com.darkshandev.sutori.data.repositories

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import app.cash.turbine.test
import com.darkshandev.sutori.PagedTestDataSources
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import com.darkshandev.sutori.fake.FakeStoryDatasources
import com.darkshandev.sutori.noopListUpdateCallback
import com.darkshandev.sutori.presentation.adapter.StoryPagedListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.junit.*
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class StoryRepositoryImplTest {
    @Mock
    private lateinit var repo: StoryRepository

    @Mock
    private lateinit var file: File

    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun tierDown() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should return paging data with 0 size`() {
        runBlocking {
            val dummyStory = FakeStoryDatasources().listTest
            val dataTest = PagingData.from(emptyList<Story>())
            val flowTest = flow {
                emit(dataTest)
            }
            val data: PagingData<Story> = PagedTestDataSources.snapshot(dummyStory)
            val config = PagingConfig(pageSize = 10, enablePlaceholders = false)
            Mockito.`when`(repo.getStoryPaged(config)).thenReturn(flowTest)

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryPagedListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,

                )

            repo.getStoryPaged(config).test {
                differ.submitData(awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            Mockito.verify(repo).getStoryPaged(config)

            Assert.assertNotNull(differ.snapshot())
            assertEquals(0, differ.snapshot().size)
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `should return paging data with 10 size`() {
        runBlocking {
            val dummyStory = FakeStoryDatasources().listTest
            val dataTest = PagingData.from(dummyStory)
            val flowTest = flow {
                emit(dataTest)
            }
            val config = PagingConfig(pageSize = 10, enablePlaceholders = false)
            Mockito.`when`(repo.getStoryPaged(config)).thenReturn(flowTest)

            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryPagedListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,

                )

            repo.getStoryPaged(config).test {
                differ.submitData(awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            Mockito.verify(repo).getStoryPaged(config)

            Assert.assertNotNull(differ.snapshot())
            assertEquals(10, differ.snapshot().size)
        }
    }

    @Test
    fun `should return failed response when post photo failed`() {
        runBlocking {
            val dummy = flow<NetworkResult<PostResponse>> {
                emit(NetworkResult.Loading())
                emit(NetworkResult.Error("fail"))
            }
            val request = PostPhotoRequest("desc", null, null)
            Mockito.`when`(repo.postPhoto(file, request)).thenReturn(dummy)
            repo.postPhoto(file, request).test {
                val firstEmission = awaitItem()
                assertTrue(firstEmission is NetworkResult.Loading)
                val secondEmission = awaitItem()
                assertTrue(secondEmission is NetworkResult.Error)
                assertEquals(secondEmission.message, "fail")
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `should return success response when post photo success`() {
        runBlocking {
            val dummy = flow {
                emit(NetworkResult.Loading())
                emit(NetworkResult.Success(PostResponse(false, "success")))
            }
            val request = PostPhotoRequest("desc", null, null)
            Mockito.`when`(repo.postPhoto(file, request)).thenReturn(dummy)
            repo.postPhoto(file, request).test {
                val firstEmission = awaitItem()
                assertTrue(firstEmission is NetworkResult.Loading)
                val secondEmission = awaitItem()
                assertTrue(secondEmission is NetworkResult.Success)
                assertEquals(secondEmission.data?.message, "success")
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}


