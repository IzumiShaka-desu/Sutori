package com.darkshandev.sutori.presentation.viewmodels

import android.location.Location
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.paging.AsyncPagingDataDiffer
import androidx.paging.PagingData
import app.cash.turbine.test
import com.darkshandev.sutori.PagedTestDataSources
import com.darkshandev.sutori.data.models.NetworkResult
import com.darkshandev.sutori.data.models.PostResponse
import com.darkshandev.sutori.data.models.StoriesResponse
import com.darkshandev.sutori.data.models.Story
import com.darkshandev.sutori.data.models.request.PostPhotoRequest
import com.darkshandev.sutori.noopListUpdateCallback
import com.darkshandev.sutori.presentation.adapter.StoryPagedListAdapter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito
import org.mockito.junit.MockitoJUnitRunner
import java.io.File

@RunWith(MockitoJUnitRunner::class)
class StoryViewModelTest {
    @get:Rule
    var instantTaskExecutorRule = InstantTaskExecutorRule()

    @Mock
    private lateinit var viewModel: StoryViewModel

    @Mock
    private lateinit var location: Location

    @Mock
    private lateinit var file: File


    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(UnconfinedTestDispatcher())

    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @After
    fun down() {
        Dispatchers.resetMain()
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `validateGetStoryAndStoryOnMap`() {
        runBlocking {
            val dummyStory = List(10) {
                Story(
                    "$it",
                    "name $it",
                    "desc $it",
                    "url $it",
                    "create at $it",
                    if ((it + 1) % 2 == 1) it.toDouble() else null,
                    if ((it + 1) % 2 == 1) it.toDouble() else null
                )
            }
            val dataTest = PagingData.from(dummyStory)
            Mockito.`when`(viewModel.storiesPaged).thenReturn(flow { emit(dataTest) })

            PagedTestDataSources.snapshot(dummyStory)
            val differ = AsyncPagingDataDiffer(
                diffCallback = StoryPagedListAdapter.DIFF_CALLBACK,
                updateCallback = noopListUpdateCallback,

                )
            viewModel.storiesPaged.test {
                differ.submitData(awaitItem())
                cancelAndConsumeRemainingEvents()
            }


            assertNotNull(differ.snapshot())
            assertEquals(10, differ.snapshot().size)

            Mockito.`when`(viewModel.storiesOnMap)
                .thenReturn(
                    MutableStateFlow(
                        NetworkResult.Success(
                            StoriesResponse(
                                false,
                                "success",
                                dummyStory
                            )
                        )
                    ).asStateFlow()
                )
            viewModel.storiesOnMap.test {
                assertEquals(10, awaitItem().data?.listStory?.size)
                cancelAndConsumeRemainingEvents()

            }
        }
    }

    @Test
    fun `verify get location behaviour`() {
        runBlocking {
            Mockito.`when`(viewModel.locationUpdates)
                .thenReturn(MutableStateFlow(null).asStateFlow())
            viewModel.locationUpdates.test {
                assertNull(awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            viewModel.setLocationUpdateListenTo(true)
            Mockito.`when`(viewModel.locationUpdates)
                .thenReturn(MutableStateFlow(location).asStateFlow())
            viewModel.locationUpdates.test {
                assertEquals(awaitItem(), location)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify set selected story`() {
        runTest {
            val dummy = Story(
                "1",
                "name 2",
                "desc 3",
                "url 4",
                "create at 4",
                null,
                null
            )
            Mockito.`when`(viewModel.selectedStory).thenReturn(MutableStateFlow(null).asStateFlow())
            viewModel.selectedStory.test {
                assertNull(awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            viewModel.setSelectedPost(dummy)
            Mockito.`when`(viewModel.selectedStory)
                .thenReturn(MutableStateFlow(dummy).asStateFlow())
            viewModel.selectedStory.test {
                assertEquals(awaitItem(), dummy)
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify set form behavior`() {
        runTest {
            Mockito.`when`(viewModel.image).thenReturn(MutableStateFlow(null).asStateFlow())
            viewModel.image.test {
                assertEquals(null, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            viewModel.setImage(file)
            Mockito.`when`(viewModel.image).thenReturn(MutableStateFlow(file).asStateFlow())
            viewModel.image.test {
                assertEquals(file, awaitItem())
                cancelAndConsumeRemainingEvents()
            }

            val dummyDefault = PostPhotoRequest("", null, null)
            Mockito.`when`(viewModel.userRequest)
                .thenReturn(MutableStateFlow(dummyDefault).asStateFlow())
            viewModel.userRequest.test {
                assertEquals(dummyDefault, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
            viewModel.setLocation(10.0, 10.0)
            viewModel.setDescription("this is desc")
            val dummyFilled = PostPhotoRequest("this is desc", 10.0, 10.0)
            Mockito.`when`(viewModel.userRequest)
                .thenReturn(MutableStateFlow(dummyFilled).asStateFlow())
            viewModel.userRequest.test {
                assertEquals(dummyFilled, awaitItem())
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @Test
    fun `verify post story failed`() {
        runBlocking {
            Mockito.`when`(viewModel.postResponse)
                .thenReturn(MutableStateFlow(NetworkResult.Initial()))
            viewModel.postResponse.test {
                assertTrue(awaitItem() is NetworkResult.Initial)
                cancelAndConsumeRemainingEvents()
            }

            Mockito.`when`(viewModel.postResponse)
                .thenReturn(MutableStateFlow(NetworkResult.Error("fail")))
            viewModel.postImage()
            Mockito.verify(viewModel).postImage()
            viewModel.postResponse.test {
                assertEquals(awaitItem().message, "fail")
                cancelAndConsumeRemainingEvents()
            }


        }
    }

    @Test
    fun `verify post story success`() {
        runBlocking {
            Mockito.`when`(viewModel.postResponse)
                .thenReturn(MutableStateFlow(NetworkResult.Initial()))
            viewModel.postResponse.test {
                assertTrue(awaitItem() is NetworkResult.Initial)
                cancelAndConsumeRemainingEvents()
            }

            Mockito.`when`(viewModel.postResponse)
                .thenReturn(MutableStateFlow(NetworkResult.Success(PostResponse(false, "success"))))
            viewModel.postImage()
            Mockito.verify(viewModel).postImage()
            viewModel.postResponse.test {
                assertEquals(awaitItem().data?.message, "success")
                cancelAndConsumeRemainingEvents()
            }
        }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    @Test
    fun `verify reset form`() {
        runTest {
            val dummyFilled = PostPhotoRequest("this is desc", 10.0, 10.0)
            Mockito.`when`(viewModel.userRequest)
                .thenReturn(MutableStateFlow(dummyFilled).asStateFlow())
            viewModel.userRequest.test {
                assertEquals(awaitItem().description, dummyFilled.description)
                cancelAndConsumeRemainingEvents()
            }
            viewModel.resetForm()
            val dummyDefaultData = PostPhotoRequest("", null, null)
            Mockito.`when`(viewModel.userRequest)
                .thenReturn(MutableStateFlow(dummyDefaultData).asStateFlow())
            viewModel.userRequest.test {
                assertEquals(awaitItem().description, dummyDefaultData.description)
                cancelAndConsumeRemainingEvents()
            }
        }
    }
}
